package com.kocuni.pianoteacher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kocuni.pianoteacher.audio.StreamAnalyzer
import com.kocuni.pianoteacher.music.MIDIPlayer
import com.kocuni.pianoteacher.music.SongTutor
import com.kocuni.pianoteacher.music.Stream
import com.kocuni.pianoteacher.music.data.MidiTable
import com.kocuni.pianoteacher.music.data.TutorableSong
import com.kocuni.pianoteacher.music.data.Voices
import com.kocuni.pianoteacher.ui.music.Tutor
import com.kocuni.pianoteacher.ui.songselection.SongSelection
import com.kocuni.pianoteacher.ui.theme.PianoTeacherTheme
import com.kocuni.pianoteacher.utils.FileManager.Companion.getSongFromJSONStream
import kotlinx.coroutines.*

/**
 * Things TODO:
 * * Indicate end or beginning of the song?
 * * Less sensitive note detection (voice detection?)
 * * Song selection menu that performs a transaction with this activity
 *   so that a rudimentary song can be loaded
 * * Piano visuals
 * * "More" information
 * * Improved layout
 */
class SongTutorViewModel() : ViewModel() {

    private var tutor: SongTutor = SongTutor()
    private var analyzer: StreamAnalyzer = StreamAnalyzer(viewModelScope)

    private val MAX_MEASURES: Int = 3
    private val midi = MIDIPlayer

    /**
     * These are only updated via StreamAnalyzer callbacks
     */
    data class SongTutorUiState(
        val autoAdvance: Boolean = false,
        val nextNotes: List<SongTutor.Block> = mutableListOf(),
        val currentNote: Int = 0, // index of the current note in nextNotes
        val playedNote: String = "C0",
        val expectedNote: String = "C0",
        val status: SongTutor.STATE = SongTutor.STATE.IDLE,
        val amplitude: Float = 0f,
        val selectedStream: Voices = Voices.SOPRANO, // TODO

        ) { }

    data class MidiState(
        val midiEnabled: Boolean = false
    )

    val controls = LambdaTutorControls(
        playToggle = { tutor.autoAdvance = !tutor.autoAdvance},
        playSet = {tutor.autoAdvance = it},
        nextMeasure = { tutor.nextMeasure() },
        prevMeasure = { tutor.prevMeasure() },
        nextChord = { tutor.next() },
        prevChord = { tutor.prev() },
        beginning = { tutor.beginning() },
        midiSet = { midiState = MidiState(it) }
    )

    var uiState by mutableStateOf(SongTutorUiState())
        private set
    var midiState by mutableStateOf(MidiState())
        private set

    /**
     * ViewModel Jobs
     */
    var analyzerJob: Job = viewModelScope.launch {
        analyzer.startAnalyzing()
    }
    var midiJob: Job = viewModelScope.launch(Dispatchers.Default) {
        midi.startDriver()
        while (isActive) {
            while (midiState.midiEnabled) {
                val chord = tutor.getCurrentNote()
                if (chord == null || !isActive) {
                    break
                } else {
                    val code = MidiTable.table[chord.notes[0].name] ?: -1
                    midi.testNote(code, 150L)
                    tutor.next()
                }
                delay(100L)
            }
        }
        midi.stopDriver()
    }

    init {
        analyzer.listener = {
            viewModelScope.launch {
                val frequency = analyzer.info.frequency
                val amplitude = analyzer.info.amplitude
                val tutorState = tutor.onUpdate(frequency)
                val songDisplay = tutor.getNextNMeasures(MAX_MEASURES)
                val playedNote: String? = tutor.getNoteName(frequency)

                val newState = SongTutorUiState(
                    autoAdvance = tutor.autoAdvance,
                    nextNotes = songDisplay.second,
                    currentNote = songDisplay.first,
                    playedNote = playedNote ?: uiState.playedNote,
                    expectedNote = "C0",
                    status = tutorState,
                    amplitude = amplitude,
                )
                uiState = newState
            }
        }
    }

    /**
     * Use callbacks to these to change the played song
     */
    val setStream = { stream: Stream ->
        tutor = SongTutor(stream)
    }

    val setSong = { song: TutorableSong ->
        tutor = SongTutor(song)
    }

    override fun onCleared() {
        super.onCleared()
        midi.stopDriver()
    }

}

class SongTutorActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onResume()

        val f = resources.openRawResource(R.raw.bach_bw101_7)

        val stream2 = getSongFromJSONStream(f)
        //val stream = SampleSongs.song1()

        val viewModel: SongTutorViewModel by viewModels()

        viewModel.setSong(stream2)
        setContent {
            TutorApp(viewModel)
        }
    }

}

@Composable
fun TutorApp(viewModel: SongTutorViewModel) {
    PianoTeacherTheme {
        val navController = rememberNavController()

        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            TutorNavHost(navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
fun TutorNavHost(
    navController: NavHostController,
    viewModel: SongTutorViewModel,
) {
    NavHost(navController = navController, startDestination = "SongTutor") {
        composable("SongTutor") {
            Tutor(viewModel, song_select = {navController.navigate("SongSelection")})
        }
        composable("SongSelection") {
            SongSelection(returnToTutor = {navController.navigate("SongTutor")})
        }
    }

}



data class LambdaTutorControls(
    val playToggle: () -> Unit = {},
    val playSet: (Boolean) -> Unit = {},
    val nextChord: () -> Unit = {},
    val prevChord: () -> Unit = {},
    val nextMeasure: () -> Unit = {},
    val prevMeasure: () -> Unit = {},
    val beginning: () -> Unit = {},
    val midiSet: (Boolean) -> Unit = {},
)

@Preview
@Composable
fun TutorControls(controls: LambdaTutorControls = LambdaTutorControls()) {
    var tutorPushed by remember { mutableStateOf(false)}
    var midiPushed by remember { mutableStateOf(false)}
    Column {
        Row(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ) {
            Button(onClick = { controls.beginning() }) {
                Text("Beginning")
            }
            Button(onClick = { controls.nextMeasure() }) {
                Text("Next Measure")
            }
            Button(onClick = { controls.prevMeasure() }) {
                Text("Prev Measure")
            }

        }
        Row {
            Surface(
                shape = MaterialTheme.shapes.small
            ) {
                IconToggleButton(
                    modifier = Modifier.background(color=Color.Green),
                    checked = tutorPushed,
                    onCheckedChange = { tutorPushed = it; controls.playSet(it) }) {
                    val tint by animateColorAsState(
                        if (tutorPushed) Color(0xFFEC407A) else Color(0xFFB0BEC5))
                    Icon(Icons.Filled.PlayArrow, contentDescription = "", tint = tint)
                }
            }
            Button(onClick = { controls.nextChord() }) {
                Text("Next Chord")
            }
            Button(onClick = { controls.prevChord() }) {
                Text("Prev Chord")
            }
            // Midi
            Surface(
                shape = MaterialTheme.shapes.small
            ) {
                IconToggleButton(
                    modifier = Modifier.background(color=Color.Green),
                    checked = midiPushed,
                    onCheckedChange = { midiPushed = it; controls.midiSet(it) }) {
                    val tint by animateColorAsState(
                        if (midiPushed) Color(0xFFEC407A) else Color(0xFFB0BEC5))
                    Icon(Icons.Filled.PlayArrow, contentDescription = "", tint = tint)
                }
            }
        }
    }
}
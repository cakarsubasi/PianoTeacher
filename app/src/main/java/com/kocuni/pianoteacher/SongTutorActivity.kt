package com.kocuni.pianoteacher

import android.content.Context
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
import com.kocuni.pianoteacher.music.data.MidiTable
import com.kocuni.pianoteacher.music.data.TutorableSong
import com.kocuni.pianoteacher.music.data.Voices
import com.kocuni.pianoteacher.ui.music.Block
import com.kocuni.pianoteacher.ui.music.NoteBlock
import com.kocuni.pianoteacher.ui.music.Tutor
import com.kocuni.pianoteacher.ui.songselection.SongSelection
import com.kocuni.pianoteacher.ui.theme.PianoTeacherTheme
import com.kocuni.pianoteacher.utils.FileManager
import com.kocuni.pianoteacher.utils.FileManager.Companion.getSongFromJSONStream
import kotlinx.coroutines.*

/**
 * Things TODO:
 * * Stream switch logic
 * * Piano played note and next notes
 * * Improved file select layout
 * * Less sensitive note detection (voice detection?)
 * * Overall layout improvements
 * * Indicate end or beginning of the song?
 * * Fix the midi stop bug
 */
class SongTutorViewModel : ViewModel() {

    var tutor: SongTutor = SongTutor()
    private var analyzer: StreamAnalyzer = StreamAnalyzer(viewModelScope)
    lateinit var fileManager: FileManager

    private val MAX_MEASURES: Int = 3
    private val MAX_BLOCKS: Int = 3
    private val midi = MIDIPlayer

    /**
     * Header text
     */
    data class SongUiState(
        val name: String = "Sample Name",
    )

    data class SongVoiceState(
        val allVoices: List<String> = listOf("SOPRANO"),
        val voice: String = "SOPRANO",
    )

    /**
     * These are only updated via StreamAnalyzer callbacks
     */
    data class SongTutorUiState(
        val autoAdvance: Boolean = false,
        val nextNotesWithMeasures: List<Block> = mutableListOf(),
        val nextNotesWithoutMeasures: List<NoteBlock> = mutableListOf(),
        val currentNoteIndex: Int = 0,
        val playedNote: Block = NoteBlock("C0"),
        val status: SongTutor.STATE = SongTutor.STATE.IDLE,
        val amplitude: Float = 0f,
        val selectedStream: Voices = Voices.SOPRANO,
        )

    data class MidiState(
        val midiEnabled: Boolean = false
    )

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

    var songState by mutableStateOf(SongUiState())
        private set
    var uiState by mutableStateOf(SongTutorUiState())
        private set
    var midiState by mutableStateOf(MidiState())
        private set
    var voiceState by mutableStateOf(SongVoiceState())
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
                    midi.playNote(code, 250L)
                    if(!tutor.next()) {
                        midiState = MidiState(false)
                    }
                }
                delay(50L)
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
                val nextNotesWithMeasures = tutor.getNextNMeasures(MAX_MEASURES)
                val playedNote = NoteBlock(tutorState.first)

                val nextNBlocks: MutableList<NoteBlock> = mutableListOf(playedNote).also {
                    it.addAll(tutor.getNextNBlocks(MAX_BLOCKS))
                }


                val newState = SongTutorUiState(
                    autoAdvance = tutor.autoAdvance,
                    nextNotesWithMeasures = nextNotesWithMeasures.second,
                    nextNotesWithoutMeasures = nextNBlocks,
                    currentNoteIndex = nextNotesWithMeasures.first,
                    playedNote = playedNote,
                    status = tutorState.second,
                    amplitude = amplitude,
                )
                uiState = newState
            }
        }
    }

    /**
     * Use callbacks to these to change the played song
     */

    val setSong = { song: TutorableSong ->
        tutor = SongTutor(song)
    }

    val setName = { name: String ->
        val newName = SongUiState(name)
        songState = newName
    }

    val getVoices = { ->
        tutor.getVoices()
    }

    val setVoice = { voice: String ->
        tutor.setVoice(voice)
    }

    override fun onCleared() {
        super.onCleared()
        midi.stopDriver()
    }

}

class SongTutorActivity : ComponentActivity() {

    private val TAG = "SongTutorActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onResume()

        val f = resources.openRawResource(R.raw.bach_bw101_7)

        val stream2 = getSongFromJSONStream(f)
        //val stream = SampleSongs.song1()

        val viewModel: SongTutorViewModel by viewModels()
        viewModel.fileManager = FileManager(getAppContext())
        viewModel.fileManager.initialize()
        viewModel.setSong(stream2)

        setContent {
            TutorApp(viewModel)
        }
    }

    val getAppContext: () -> Context? = {
        this.applicationContext
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
            Tutor(viewModel = viewModel, song_select = {
                navController.navigate("SongSelection")})
        }
        composable("SongSelection",) {
            SongSelection(viewModel = viewModel, returnToTutor = {
                navController.navigateUp()
            })
        }
    }

}


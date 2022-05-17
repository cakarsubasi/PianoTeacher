package com.kocuni.pianoteacher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kocuni.pianoteacher.audio.StreamAnalyzer
import com.kocuni.pianoteacher.music.MIDIPlayer
import com.kocuni.pianoteacher.music.data.SampleSongs
import com.kocuni.pianoteacher.music.SongTutor
import com.kocuni.pianoteacher.music.Stream
import com.kocuni.pianoteacher.music.data.TutorableSong
import com.kocuni.pianoteacher.ui.music.Piano
import com.kocuni.pianoteacher.ui.songselection.SongSelection
import com.kocuni.pianoteacher.ui.theme.PianoTeacherTheme
import com.kocuni.pianoteacher.utils.FileManager.Companion.getSongFromJSONStream
import com.kocuni.pianoteacher.utils.JSONParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStream
import java.lang.NullPointerException

/**
 * Things TODO:
 * * Indicate end or beginning of the song?
 * * Less sensitive note detection (voice detection?)
 * * Adaptive colors for notes
 * * Song selection menu that performs a transaction with this activity
 *   so that a rudimentary song can be loaded
 * * Piano visuals
 * * "More" information
 * * Improved layout
 */
class SongTutorViewModel(var tutor: SongTutor,var analyzer: StreamAnalyzer) : ViewModel() {

    private val MAX_MEASURES: Int = 3
    val midi = MIDIPlayer()

    data class SongTutorUiState(
        val autoAdvance: Boolean = false,
        val nextNotes: List<SongTutor.Block> = mutableListOf(),
        val currentNote: Int = 0, // index of the current note in nextNotes
        val playedNote: String = "C0",
        val expectedNote: String = "C0",
        val status: SongTutor.STATE = SongTutor.STATE.IDLE,

        ) { }

    val controls = LambdaTutorControls(
        playToggle = { tutor.autoAdvance = !tutor.autoAdvance},
        playSet = {tutor.autoAdvance = it},
        nextMeasure = { tutor.nextMeasure() },
        prevMeasure = { tutor.prevMeasure() },
        nextChord = { tutor.next() },
        prevChord = { tutor.prev() },
        beginning = { tutor.beginning() },
    )

    var uiState by mutableStateOf(SongTutorUiState())
        private set

    init {
        viewModelScope.launch {
            analyzer.startAnalyzing()
        }

        analyzer.listener = {
            viewModelScope.launch {
                val frequency = analyzer.info.frequency
                val tutorState = tutor.beginTutor(frequency)
                val songDisplay = tutor.getNextNMeasures(MAX_MEASURES)
                val playedNote: String? = tutor.getNoteName(frequency)

                val newState = SongTutorUiState(
                    autoAdvance = tutor.autoAdvance,
                    nextNotes = songDisplay.second,
                    currentNote = songDisplay.first,
                    playedNote = playedNote ?: uiState.playedNote,
                    expectedNote = "C0",
                    status = tutorState)
                uiState = newState
            }
        }

        /**
         * TODO MidiPlayer
         */
        viewModelScope.launch {
            while (true) {
                // midi.testNote()
                delay(1000L)
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

}

class SongTutorActivity : ComponentActivity() {

    lateinit var analyzer: StreamAnalyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val f = resources.openRawResource(R.raw.bach_bw101_7)

        val stream2 = getSongFromJSONStream(f)
        val stream = SampleSongs.song1()

        analyzer = StreamAnalyzer(lifecycleScope)
        val tutor = SongTutor(stream2)
        val viewModel = SongTutorViewModel(tutor = tutor, analyzer = analyzer)

        setContent {
            TutorApp(viewModel)
        }
    }

    /**
     * TODO: replace this with a viewmodel control to more robustly begin
     * and end recordings.
     */
    override fun onPause() {
        super.onPause()
        analyzer.endAnalyzing()
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
            Column() {
                TutorNavHost(navController = navController, viewModel = viewModel)
            }
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

@Composable
fun Tutor(
    viewModel: SongTutorViewModel,
    song_select: () -> Unit = {},
) {
    var uiState = viewModel.uiState
    LaunchedEffect(viewModel.uiState) {
        uiState = viewModel.uiState
    }
    val status: String =
    when (uiState.status) {
        SongTutor.STATE.IDLE -> "idle"
        SongTutor.STATE.FALSE -> "false"
        else -> "true" }
    Column() {
        // top menu bar
        TopAppBar() {
            FloatingActionButton(onClick = song_select) {

            }
        }
        Row {

        }
        // note stream
        NoteList(uiState.nextNotes, currentPos = uiState.currentNote)
        // detected note
        Row {
            Note(SongTutor.NoteBlock(uiState.playedNote))
            Text( text = status )
        }

        // piano
        Card() {
            Piano()
        }

        // controls
        Row () {
            TutorControls(controls = viewModel.controls)
        }

    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun DefaultPreview3() {
    PianoTeacherTheme {
        Greeting("Android")
        Column {
            Row() {
                TopAppBar() {
                    IconButton(
                        onClick = { /*TODO*/ },

                    ) {

                    }
                }
            }
            Card(
                modifier = Modifier.padding(4.dp),
                elevation = 10.dp,
            ) {
                NoteList()
            }
            Card {
                Note()
            }
            Card {
                Piano()
            }
            Card {
                TutorControls()
            }
        }

    }
}

/**
 * TODO: correct current note
 */
@Preview(showBackground = true)
@Composable
fun NoteList(
    noteList: List<SongTutor.Block> = SongTutor(stream = SampleSongs.song1()).endToEnd,
    currentPos: Int = 1,
) {
    LazyRow(
        contentPadding = PaddingValues(1.dp)
    ) {
        items(noteList.size) { index ->
                Note(noteList[index], (index == currentPos))
        }
    }
}

/**
 * TODO: Adaptive coloring
 */
@Preview
@Composable
fun Note(note: SongTutor.Block = SongTutor.NoteBlock(), current: Boolean = false) {
    Card(
        modifier = Modifier.size(40.dp).padding(all = 4.dp),
        elevation = 2.dp,
        backgroundColor = if (current) Color.Green else note.color,

    ) {
        Surface(
            modifier = Modifier.padding(all = 4.dp),
            shape= MaterialTheme.shapes.small,
            elevation= 2.dp,
            color = note.color,) {
            Text(
                text = note.name,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun Status() {
    Column {
        NoteList()
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
)

@Preview
@Composable
fun TutorControls(controls: LambdaTutorControls = LambdaTutorControls()) {
    var pushed by remember { mutableStateOf(false)}
    Column {
        Row {
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
                    checked = pushed,
                    onCheckedChange = { pushed = it; controls.playSet(it) }) {
                    val tint by animateColorAsState(
                        if (pushed) Color(0xFFEC407A) else Color(0xFFB0BEC5))
                    Icon(Icons.Filled.PlayArrow, contentDescription = "", tint = tint)
                }
            }
            Button(onClick = { controls.nextChord() }) {
                Text("Next Chord")
            }
            Button(onClick = { controls.prevChord() }) {
                Text("Prev Chord")
            }
        }
    }
}
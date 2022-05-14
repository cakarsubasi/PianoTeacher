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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.kocuni.pianoteacher.audio.StreamAnalyzer
import com.kocuni.pianoteacher.music.SampleSongs
import com.kocuni.pianoteacher.music.SongTutor
import com.kocuni.pianoteacher.ui.theme.PianoTeacherTheme
import kotlinx.coroutines.launch

/**
 * Things TODO:
 * * Indicate end or beginning of the song?
 * * Functional current note display and last detected note display
 * * Adaptive colors for notes
 * * Song selection menu that performs a transaction with this activity
 *   so that a rudimentary song can be loaded
 * * Piano visuals
 * * "More" information
 * * Improved layout
 */
class SongTutorViewModel(var tutor: SongTutor,var analyzer: StreamAnalyzer) : ViewModel() {

    val MAX_MEASURES: Int = 2

    data class SongTutorUiState(
        val autoAdvance: Boolean = false,
        val nextNotes: List<SongTutor.NoteBlock> = mutableListOf(),
        val currentNote: Int = 0,
        val playedNote: Int = -1,
        val expectedNote: String = "C0",
        val status: SongTutor.STATE = SongTutor.STATE.IDLE,

        ) { }

    // TODO
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
        analyzer.startAnalyzing()

        analyzer.listener = {
            viewModelScope.launch {
                val tutorState = tutor.beginTutor(analyzer.info.frequency)
                val songDisplay = tutor.getNextNMeasures(2)
                val playedNote = -1
                val newState = SongTutorUiState(
                    autoAdvance = tutor.autoAdvance,
                    nextNotes = songDisplay.second,
                    currentNote = songDisplay.first,
                    playedNote = playedNote,
                    expectedNote = "C0",
                    status = tutorState)
                uiState = newState
            }
        }
    }
}

class SongTutorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val analyzer = StreamAnalyzer(lifecycleScope)
        val tutor = SongTutor(stream = SampleSongs.song1())
        val viewModel = SongTutorViewModel(tutor = tutor, analyzer = analyzer)

        setContent {
            PianoTeacherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column() {
                        Greeting("Android")
                        Status()
                        Tutor(viewModel)
                    }

                }

            }
        }

    }
}

@Composable
fun Tutor(
    viewModel: SongTutorViewModel
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
        // top menu bar

        // note stream
        NoteList(uiState.nextNotes, currentPos = uiState.currentNote)
        // detected note
        Row {
            Note(uiState.expectedNote)
            Text( text = status )
        }

        // piano

        // controls
        TutorControls(controls = viewModel.controls)
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun DefaultPreview3() {
    PianoTeacherTheme {
        Greeting("Android")
        Column {
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
    noteList: List<SongTutor.NoteBlock> = SongTutor(stream = SampleSongs.song1()).endToEnd,
    currentPos: Int = 1,
) {
    LazyRow(
        contentPadding = PaddingValues(1.dp)
    ) {
        items(noteList.size) { index ->
                Note(noteList[index].name, (index == currentPos))
        }
    }
}

/**
 * TODO: Adaptive coloring
 */
@Preview
@Composable
fun Note(str: String = "C4", current: Boolean = false) {
    Card(
        elevation = 2.dp,
        backgroundColor = Color.Blue

    ) {
        Surface(
            modifier = Modifier.padding(all = 4.dp),
            shape= MaterialTheme.shapes.small,
            elevation= 2.dp,
            color = if (current) Color.Green else Color.Blue,) {
            Text(
                text = str,
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

@Composable
fun ExpectedNote() {

}

@Preview
@Composable
fun Piano() {
    Icon(
        painter = painterResource(id = R.drawable.ic_piano88key),
        contentDescription = null,
    )
}

data class LambdaTutorControls(
    val playToggle: () -> Unit = {},
    val playSet: (Boolean) -> Unit = {},
    val nextChord: () -> Unit = {},
    val prevChord: () -> Unit = {},
    val nextMeasure: () -> Unit = {},
    val prevMeasure: () -> Unit = {},
    val beginning: () -> Unit = {},
) {}

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
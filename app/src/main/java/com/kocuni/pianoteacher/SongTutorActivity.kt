package com.kocuni.pianoteacher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.kocuni.pianoteacher.audio.StreamAnalyzer
import com.kocuni.pianoteacher.music.SampleSongs
import com.kocuni.pianoteacher.music.SongTutor
import com.kocuni.pianoteacher.music.Stream
import com.kocuni.pianoteacher.ui.theme.PianoTeacherTheme
import kotlinx.coroutines.launch


class SongTutorViewModel(var tutor: SongTutor,var analyzer: StreamAnalyzer) : ViewModel() {

    val MAX_MEASURES: Int = 2

    data class SongTutorUiState(
        val autoAdvance: Boolean = false,
        val nextNotes: List<SongTutor.NoteBlock> = mutableListOf(),
        val playedNote: Int = -1,
        val expectedNote: String = "C0",
        val status: SongTutor.STATE = SongTutor.STATE.IDLE,

        ) { }

    var uiState by mutableStateOf(SongTutorUiState())
        private set

    init {
        analyzer.startAnalyzing()
        analyzer.listener = {
            viewModelScope.launch {
                val tutorState = tutor.beginTutor(analyzer.info.frequency)
                val songDisplay = tutor.getNextNMeasures(2)
                val newState = SongTutorUiState(
                    autoAdvance = false,
                    playedNote = -1,
                    expectedNote = "C0",
                    status = tutorState)
                uiState = newState
            }
        }
    }

    fun updateUiState() {
        val autoAdvance = false
        val playedNote = -1;
        val expectedNote = "C0"
        val displayedStream: List<SongTutor.NoteBlock> = mutableListOf()
        val tutorState = SongTutor.STATE.IDLE
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
                        Greeting3("Android")
                        Status()
                    }

                }
            }
        }

    }
}

@Composable
fun Greeting3(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    PianoTeacherTheme {
        Greeting3("Android")
    }
}

@Preview
@Composable
fun NoteList(notelist: List<SongTutor.NoteBlock> = SongTutor(stream = SampleSongs.song1()).endToEnd) {
    LazyRow(
        contentPadding = PaddingValues(1.dp)
    ) {
        items(notelist.size) {index ->
                val current = 1
                Note(notelist[index].note.notes[0].toString(), (index == current))
        }
    }
}

@Preview
@Composable
fun Note(str: String = "C4", current: Boolean = false) {
    Surface(
        modifier = Modifier.padding(all = 4.dp),
        shape= MaterialTheme.shapes.large,
        elevation= 2.dp,
        color = if (current) Color.Green else Color.Blue,) {
        Text(
            text = str,
        )
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

@Composable
fun Piano() {

}
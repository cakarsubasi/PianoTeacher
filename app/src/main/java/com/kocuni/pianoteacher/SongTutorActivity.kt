package com.kocuni.pianoteacher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        val tutor = SongTutor(scope = lifecycleScope, stream = SampleSongs.song1())
        val viewModel = SongTutorViewModel(tutor = tutor, analyzer = analyzer)

        setContent {
            PianoTeacherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting3("Android")
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


@Composable
fun Piano() {

}
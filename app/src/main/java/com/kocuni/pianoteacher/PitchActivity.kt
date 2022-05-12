package com.kocuni.pianoteacher

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.kocuni.pianoteacher.audio.StreamAnalyzer
import com.kocuni.pianoteacher.music.SampleSongs
import com.kocuni.pianoteacher.music.SongTutor
import com.kocuni.pianoteacher.ui.theme.PianoTeacherTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PitchViewModel(val analyzer: StreamAnalyzer): ViewModel() {
    private val TAG = "PitchViewModel"

    data class PitchUiState(
        val isRecording: Boolean = false,
        val amplitude: Float = 0.0F,
        val pitch: Float = -1.0F,
        val confidence: Float = 0.0F,
        val note: String? = null
    )

    var uiState by mutableStateOf(PitchUiState())
        private set
    //val analyzer: StreamAnalyzer = StreamAnalyzer(viewModelScope)

    init {
        Log.d(TAG, "pitch view model")
        analyzer.startAnalyzing()
        analyzer.listener = {
            viewModelScope.launch {
                Log.d(TAG, "callback")
                val info = analyzer.info
                val newState = PitchUiState(true, info.amplitude, info.frequency, info.confidence, "")
                uiState = newState
            }
        }
    }


}

class PitchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val analyzer = StreamAnalyzer(lifecycleScope)
        val viewModel = PitchViewModel(analyzer)


        setContent {
            PianoTeacherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //Greeting2("Android", viewModel.uiState)
                    PitchPanel(viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

    }


}

@Composable
fun PitchPanel(
    viewModel: PitchViewModel,
) {
    var uiState = viewModel.uiState
    LaunchedEffect(viewModel.uiState) {
        uiState = viewModel.uiState
    }
    PitchInfo(uiState)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    val st = SampleSongs.song1()

    PianoTeacherTheme {
        PitchInfo(pitchState = PitchViewModel.PitchUiState())
    }
}

@Composable
fun PitchInfo(pitchState: PitchViewModel.PitchUiState) {
    Column {
        Text(text = "Recording? : ${pitchState.isRecording}")
        Text(text = "Avg  : ${pitchState.amplitude}")
        Text(text = "Peak : ")
        Text(text = "Pitch: ${pitchState.pitch}")
        Text(text = "Prob : ${pitchState.confidence}")
    }
}

@Preview
@Composable
fun NoteList() {
    val notelist = SongTutor(SampleSongs.song1()).endToEnd
        LazyRow {
            items(notelist) {
                note -> Note(note.note.toString())
        }
    }
}

@Preview
@Composable
fun Note(str: String = "C4") {
    Surface(shape= MaterialTheme.shapes.large, elevation= 1.dp,
    color = Color.Blue) {
        Text(
            text = str,
            )
    }

}
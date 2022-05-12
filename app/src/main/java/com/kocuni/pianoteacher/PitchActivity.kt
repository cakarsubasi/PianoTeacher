package com.kocuni.pianoteacher

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kocuni.pianoteacher.audio.StreamAnalyzer
import com.kocuni.pianoteacher.ui.theme.PianoTeacherTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PitchViewModel: ViewModel() {
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
    val analyzer: StreamAnalyzer = StreamAnalyzer(viewModelScope)

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

        val viewModel = PitchViewModel()

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

@Composable
fun Greeting2(name: String, uiState: PitchViewModel.PitchUiState) {
    Column {
        Text(text = "Hello $name!")
        PitchInfo(uiState)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    PianoTeacherTheme {
        Greeting2("Android", uiState = PitchViewModel.PitchUiState())
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

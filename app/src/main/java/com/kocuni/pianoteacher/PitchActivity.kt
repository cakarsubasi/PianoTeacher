package com.kocuni.pianoteacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.*
import com.kocuni.pianoteacher.audio.StreamAnalyzer
import com.kocuni.pianoteacher.databinding.ActivityPitchBinding
import kotlinx.coroutines.flow.*
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

    private val _uiState = MutableStateFlow(PitchUiState())
    val uiState: StateFlow<PitchUiState> = _uiState.asStateFlow()
    val analyzer = StreamAnalyzer()

    init {
        analyzer.listener = {
            viewModelScope.launch {
                val info = analyzer.info
                val newState = PitchUiState(true, info.amplitude, info.frequency, info.confidence, "")
                _uiState.update {
                    newState
                }
            }
        }
    }

}

class PitchActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPitchBinding
    private val viewModel: PitchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pitch)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    val view: TextView = findViewById(R.id.textView3)
                    view.text = it.pitch.toString()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.analyzer.endAnalyzing()
    }

    override fun onResume() {
        super.onResume()
        viewModel.analyzer.startAnalyzing()
    }
}
package com.kocuni.pianoteacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kocuni.pianoteacher.audio.StreamAnalyzer

class PitchActivity : AppCompatActivity() {

    lateinit var analyzer: StreamAnalyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pitch)

        analyzer = StreamAnalyzer()
    }

    override fun onPause() {
        super.onPause()
        analyzer.endAnalyzing()
    }

    override fun onResume() {
        super.onResume()
        analyzer.startAnalyzing()
    }
}
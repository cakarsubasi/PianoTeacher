package com.kocuni.pianoteacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.Manifest
import android.content.pm.PackageManager
import android.view.MotionEvent
import android.widget.Switch
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.kocuni.pianoteacher.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    external fun stringFromJNI(): String
    external fun startEngine(): Boolean
    external fun stopEngine()
    external fun setRecording(isRecording: Boolean)
    external fun setPlaying(isPlaying: Boolean)

    companion object {
        // Used to load the 'pianoteacher' library on application startup.
        init {
            System.loadLibrary("pianoteacher")
        }
    }

    private lateinit var binding: ActivityMainBinding
    private val micRequest = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val button: Button = findViewById(R.id.closeButton);
        button.setOnClickListener { _ ->
            binding.sampleText.text = "Button pressed"
        }

        if (!isRecordPermissionGranted()) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),
            micRequest
            )
        }

        val recordArea: View = findViewById(R.id.recordArea)
        recordArea.setOnTouchListener { _, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {binding.sampleText.text = "Recording"; setRecording(true)}
                MotionEvent.ACTION_UP -> {binding.sampleText.text = "Idle"; setRecording(false)}
            }
            true;
        }

        val playArea: View = findViewById(R.id.playArea)
        playArea.setOnTouchListener { _, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {binding.sampleText.text = "Playing"; setPlaying(true)}
                MotionEvent.ACTION_UP -> {binding.sampleText.text = "Idle"; setPlaying(true)}
            }
            true;
        }

        val streamSwitch: Switch = findViewById(R.id.streamSwitch)
        streamSwitch.setOnCheckedChangeListener { _, checked ->
            when(checked) {
                true -> startEngine()
                false -> stopEngine()
            }

        }

        startEngine()

    }


    private fun isRecordPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this,
        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * A native method that is implemented by the 'pianoteacher' native library,
     * which is packaged with this application.
     */

}
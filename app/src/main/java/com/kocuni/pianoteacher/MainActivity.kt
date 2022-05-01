package com.kocuni.pianoteacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.view.MotionEvent
import android.widget.Switch
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.kocuni.pianoteacher.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val micRequest = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val button: Button = findViewById(R.id.closeButton);
        button.setOnClickListener { _ ->
            val intent = Intent(this, SongActivity::class.java).apply {  }
            startActivity(intent)
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
                MotionEvent.ACTION_DOWN -> {binding.sampleText.text = "Recording"; JNIBridge.setRecording(true)}
                MotionEvent.ACTION_UP -> {binding.sampleText.text = "Idle"; JNIBridge.setRecording(false)}
            }
            true
        }

        val playArea: View = findViewById(R.id.playArea)
        playArea.setOnTouchListener { _, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {binding.sampleText.text = "Playing"; JNIBridge.setPlaying(true)}
                MotionEvent.ACTION_UP -> {binding.sampleText.text = "Idle"; JNIBridge.setPlaying(true)}
            }
            true;
        }

        val streamSwitch: Switch = findViewById(R.id.streamSwitch)
        streamSwitch.setOnCheckedChangeListener { _, checked ->
            when(checked) {
                true -> JNIBridge.startEngine()
                false -> JNIBridge.stopEngine() }
        }
    }


    private fun isRecordPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this,
        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

}
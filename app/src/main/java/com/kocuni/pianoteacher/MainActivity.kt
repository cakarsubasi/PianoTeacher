package com.kocuni.pianoteacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.view.MotionEvent
import android.widget.Switch
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.kocuni.pianoteacher.audio.PlaybackManager
import com.kocuni.pianoteacher.audio.RecordingManager
import com.kocuni.pianoteacher.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var recorder: AudioRecord
    private val micRequest = 0;
    val minBuffSize = 480000;

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
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val executor: ExecutorService = Executors.newFixedThreadPool(10)

        val manager = RecordingManager()
        //manager.startRecording()
        val manager2 = PlaybackManager()
        manager2.testBuffer()

        val recordArea: View = findViewById(R.id.recordArea)
        recordArea.setOnTouchListener { _, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {binding.sampleText.text = "Recording"; }
                MotionEvent.ACTION_UP -> {binding.sampleText.text = "Idle"; }
            }
            true
        }

        val playArea: View = findViewById(R.id.playArea)
        playArea.setOnTouchListener { _, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {binding.sampleText.text = "Playing"; runBlocking {
                    launch {
                        manager2.write()

                    }
                }
                }
                MotionEvent.ACTION_UP -> {binding.sampleText.text = "Idle"; }
            }
            true;
        }

        /*
        val streamSwitch: Switch = findViewById(R.id.streamSwitch)
        streamSwitch.setOnCheckedChangeListener { _, checked ->
            when(checked) {
                true -> JNIBridge.startEngine()
                false -> JNIBridge.stopEngine() }
        }
         */
    }


    private fun isRecordPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this,
        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

}
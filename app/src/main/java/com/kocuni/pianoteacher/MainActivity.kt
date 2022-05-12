package com.kocuni.pianoteacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioRecord
import android.view.MotionEvent
import androidx.core.app.ActivityCompat
import com.kocuni.pianoteacher.audio.StreamAnalyzer
import com.kocuni.pianoteacher.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var recorder: AudioRecord
    private val micRequest = 0;
    val minBuffSize = 480000;

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!isRecordPermissionGranted()) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),
                micRequest
            )
        }

        //val streamAnalyzer = StreamAnalyzer(MainScope())

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val songButton: Button = findViewById(R.id.songButton);
        songButton.setOnClickListener { _ ->
            val intent = Intent(this, SongTutorActivity::class.java).apply {  }
            startActivity(intent)
        }

        val pitchButton: Button = findViewById(R.id.pitchButton);
        pitchButton.setOnClickListener { _ ->
            val intent = Intent(this, PitchActivity::class.java).apply {  }
            startActivity(intent)
        }
        val serverButton: Button = findViewById(R.id.serverButton);
        serverButton.setOnClickListener { _ ->
            val intent = Intent(this, ServerActivity::class.java).apply {  }
            startActivity(intent)
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

        /*
        val recordArea: View = findViewById(R.id.recordArea)
        recordArea.setOnTouchListener { _, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {binding.sampleText.text = "Recording"; streamAnalyzer.startAnalyzing()}
                MotionEvent.ACTION_UP -> {binding.sampleText.text = "Idle"; streamAnalyzer.endAnalyzing()}
            }
            true
        }

        val playArea: View = findViewById(R.id.playArea)
        playArea.setOnTouchListener { _, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {binding.sampleText.text = "Playing";

                }
                MotionEvent.ACTION_UP -> {binding.sampleText.text = "Idle"; }
            }
            true;
        }
        */


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
package com.kocuni.pianoteacher

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.nio.ByteBuffer
import java.util.*

class SongActivity : AppCompatActivity() {

    private val TAG = "SongActivityDebug"
    val minBuffSize = 2048
    var arr: ShortArray = ShortArray(minBuffSize)
    lateinit var recorder: AudioRecord




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_song)

        val text: View = findViewById(R.id.textView)

        val timer: Timer = Timer()

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

        recorder = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(48000)
                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBuffSize * 2)
            .build()

        recorder.startRecording()
        //var buffer: ShortArray = ShortArray(4096)
        //recorder.read(buffer, 0, 2048)


        timer.scheduleAtFixedRate(
            TestTask(recorder),
            1000L,
            2000L
        )

    }

    class TestTask(private val recorder: AudioRecord) : TimerTask() {
        private val TAG = "SongActivityDebug"
        private var num = 0
        var buffer: ShortArray = ShortArray(4096)
        override fun run() {
            //Log.d(TAG, "Timer event. ${JNIBridge.marshallTest()}")
            recorder.read(buffer, 0, 2048)
            var total = 0
            for (i in 0..2047) {
                total += buffer.get(i)
            }

            Log.d(TAG, "Timer event: $total")
        }

    }

    class TestTask2 : TimerTask() {
        private val TAG = "SongActivityDebug"
        private var num = 0
        override fun run() {
            //val x = JNIBridge.marshallTest()
           // Log.d(TAG, "Timer event. ${x}")

        }
    }

    fun setText(f: Float) {
        val text: TextView = findViewById(R.id.textView)
        text.text = f.toString()
    }

}
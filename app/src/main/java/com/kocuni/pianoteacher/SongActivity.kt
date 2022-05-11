package com.kocuni.pianoteacher

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File

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
        val filename = "test.txt"
        val file = File(applicationContext.filesDir, filename)
        Log.d(TAG, applicationContext.filesDir.toString())


        getTestJSON()

    }

    fun getTestJSON() {
        val context = applicationContext
        val buffer = ByteArray(100_000)
        resources.openRawResource(R.raw.example_song).read(buffer)
        val jsonstr = String(buffer)

        Log.d(TAG, jsonstr)
    }

    fun setText(f: Float) {
        val text: TextView = findViewById(R.id.textView)
        text.text = f.toString()
    }

}
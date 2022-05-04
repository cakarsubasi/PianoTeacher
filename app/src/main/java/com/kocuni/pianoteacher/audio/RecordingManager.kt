package com.kocuni.pianoteacher.audio

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.*
import androidx.core.app.ActivityCompat
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.sinh

@SuppressLint("MissingPermission")
class RecordingManager {

    val sampleRate = 44100
    val buffersize = sampleRate * 4
    val buffer = FloatArray(buffersize)
    var recorder: AudioRecord = AudioRecord.Builder()
        .setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        .setAudioFormat(
            AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .build()
        )
        .setBufferSizeInBytes(buffersize * 4)
        .build();

    fun startRecording() {
        recorder.startRecording()
    }

    fun stopRecording() {
        recorder.stop()
    }

    suspend fun read() {
            recorder.read(buffer, 0, buffersize, AudioRecord.READ_BLOCKING)

    }

}

class PlaybackManager {
    val sampleRate = 44100
    val buffersize = sampleRate * 4
    val buffer = FloatArray(buffersize)
    var player: AudioTrack = AudioTrack.Builder()
        .setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build())
        .setAudioFormat(
            AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()
        )
        .setBufferSizeInBytes(buffersize * 4)
        .build();

    fun startPlaying() {
        player.play()
    }

    fun stopPlaying() {
        player.stop()
    }

    fun testBuffer() {
        val freq = 2.0*PI*440.0/sampleRate.toDouble();

        for (i in 0 until buffersize) {
            var phase = freq*i.toDouble()
            buffer[i] = (0.5*sin(phase)).toFloat()
        }
    }

    suspend fun write() {
        player.play()
        player.write(buffer, 0, buffersize, AudioTrack.WRITE_BLOCKING)
        player.stop()
    }

}
package com.kocuni.pianoteacher.audio

import android.annotation.SuppressLint
import android.media.*
import android.util.Log
import kotlin.math.PI
import kotlin.math.sin


@SuppressLint("MissingPermission")
class RecordingManager {

    private val TAG = "RecordingManager"

    private val BUFFER_SIZE_FACTOR = 2

    val sampleRate = 44100
    val buffersize = 1024
    val buffer = FloatArray(buffersize)

    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_FLOAT

    private val BUFFER_SIZE = AudioRecord.getMinBufferSize(sampleRate,
        CHANNEL_CONFIG,AUDIO_FORMAT) * BUFFER_SIZE_FACTOR

    var recorder: AudioRecord = AudioRecord.Builder()
        .setAudioSource(MediaRecorder.AudioSource.MIC)
        .setAudioFormat(
            AudioFormat.Builder()
                .setEncoding(AUDIO_FORMAT)
                .setSampleRate(sampleRate)
                .setChannelMask(CHANNEL_CONFIG)
                .build()
        )
        .setBufferSizeInBytes(BUFFER_SIZE)
        .build()

    suspend fun read() {
            recorder.startRecording()
            val result = recorder.read(buffer, 0, buffersize, AudioRecord.READ_BLOCKING)
            Log.d(TAG, "result: $result")
            Log.d(TAG, "${buffer.average()}")
            recorder.stop()
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

    fun render(source: FloatArray) {
        //while (source.size)
    }

    suspend fun write() {
        player.play()
        player.write(buffer, 0, buffersize, AudioTrack.WRITE_BLOCKING)
        player.stop()
    }

}
package com.kocuni.pianoteacher.audio

import android.annotation.SuppressLint
import android.media.*
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.PI
import kotlin.math.sin


@SuppressLint("MissingPermission")
class RecordingManager(val bufferSize: Int = 1024) {

    private val TAG = "RecordingManager"

    val channel = Channel<ShortArray>()


    val SAMPLE_RATE = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM)

    private val BUFFER_SIZE_FACTOR = 2
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE,
        CHANNEL_CONFIG,AUDIO_FORMAT) * BUFFER_SIZE_FACTOR


    val isRecording: AtomicBoolean = AtomicBoolean(true)

    var recorder: AudioRecord = AudioRecord.Builder()
        .setAudioSource(MediaRecorder.AudioSource.MIC)
        .setAudioFormat(
            AudioFormat.Builder()
                .setEncoding(AUDIO_FORMAT)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(CHANNEL_CONFIG)
                .build()
        )
        .setBufferSizeInBytes(BUFFER_SIZE)
        .build()


    suspend fun startStreaming() {
        try {
            val buffer = ShortArray(BUFFER_SIZE)
            Log.d(TAG, "Creating AudioRecord")

            val recorder = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE)

            Log.d(TAG, "start recording")
            recorder.startRecording()

            while (isRecording.get()) {

                val readSize = recorder.read(buffer, 0, bufferSize)
                Log.d(TAG, "Read $readSize frames.")
                Log.d(TAG, "avg: ${buffer.average()}")

                val shortBuffer = ShortArray(bufferSize)
                for (i in shortBuffer.indices) {
                    shortBuffer[i] = buffer[i]
                }
                channel.send(shortBuffer)
            }
            recorder.stop()

            recorder.release()

        } catch (e: Exception) {
            Log.e(TAG, "Exception: $e")
        }
    }

    suspend fun stopStreaming() {
        isRecording.set(false)
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
        .build()

    fun startPlaying() {
        player.play()
    }

    fun stopPlaying() {
        player.stop()
    }

    fun testBuffer() {
        val freq = 2.0*PI*440.0/sampleRate.toDouble()

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
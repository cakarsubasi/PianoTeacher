package com.kocuni.pianoteacher.audio

import android.icu.text.AlphabeticIndex
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Manage and analyze an incoming audio stream
 */
class StreamAnalyzer {

    val bufferSize = 1024
    val TAG = "StreamAnalyzer"

    // user double buffering
    val bufferBack = FloatArray(bufferSize)
    var bufferFront = FloatArray(bufferSize)

    var isRecording: Boolean = false

    private val streamScope = MainScope()
    private lateinit var recordJob: Job
    private lateinit var analyzeJob: Job

    val manager: RecordingManager = RecordingManager()

    /**
     * Start threads that record and analyze
     */
    fun startAnalyzing() {
        var i = 0
        val channel = Channel<FloatArray>()
        var ready: AtomicBoolean = AtomicBoolean(false)
        recordJob = streamScope.launch(Dispatchers.Default) {

            while (isActive) {
                delay(500L)
                i++
                Log.d(TAG, "record job $i")
                manager.read()
                // copy
                bufferFront = manager.buffer.clone()
                channel.send(bufferFront)

                ready.set(true)
            }
        }

        analyzeJob = streamScope.launch(Dispatchers.Default) {
            delay(500L)
            while (isActive) {
                val buffer = channel.receive()
                Log.d(TAG, "analyze job")
                analyzeBuffer(buffer)
                /*
                if (ready.get()) {
                    Log.d(TAG, "analyze job $i")
                    ready.set(false)
                } else {
                    delay(50L)
                }
                */
            }

        }
    }

    /**
     * Stop the threads
     */
    fun endAnalyzing() = runBlocking {
        recordJob.cancelAndJoin()
        analyzeJob.cancelAndJoin()
    }
    /*
    fun bufferStats() {
        var peak_amp = 0.0F
        var avg_amp = 0.0F

        for (i in 0 until sampleRate/2) {
            var elem = buffer[i]*buffer[i]
            if (elem > peak_amp) peak_amp = elem
            avg_amp += elem
        }
        avg_amp = 2.0F*avg_amp/sampleRate

        Log.d(TAG, "Peak: $peak_amp\nAvg:  $avg_amp")

    } */

    fun analyzeBuffer(buffer: FloatArray) {
        var peak_amp = 0.0F
        var avg_amp = 0.0F

        for (i in buffer.indices) {
            var elem = buffer[i]*buffer[i]
            if (elem > peak_amp) peak_amp = elem
            avg_amp += elem
        }
        avg_amp = 2.0F*avg_amp/buffer.size

        Log.d(TAG, "Peak: $peak_amp\nAvg:  $avg_amp")
    }

    /*
    fun amplitudes(): Flow<Float> {

    }
    */
}
package com.kocuni.pianoteacher.audio

import android.icu.text.AlphabeticIndex
import android.util.Log
import kotlinx.coroutines.*
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
    val bufferFront = FloatArray(bufferSize)

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
        var ready: AtomicBoolean = AtomicBoolean(false)
        recordJob = streamScope.launch(Dispatchers.Default) {

            while (isActive) {
                delay(500L)
                i++
                Log.d(TAG, "record job $i")
                manager.read()
                // copy
                ready.set(true)
            }
        }

        analyzeJob = streamScope.launch(Dispatchers.Default) {
            delay(500L)
            while (isActive) {
                if (ready.get()) {
                    Log.d(TAG, "analyze job $i")
                    ready.set(false)
                } else {
                    delay(50L)
                }

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

    fun analyzeBuffer() {

    }

    /*
    fun amplitudes(): Flow<Float> {

    }
    */
}
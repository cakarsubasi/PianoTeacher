package com.kocuni.pianoteacher.audio

import android.icu.text.AlphabeticIndex
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import be.tarsos.dsp.pitch.FastYin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Manage and analyze an incoming audio stream
 */
class StreamAnalyzer(scope: CoroutineScope, val bufferSize: Int = 1024) {

    data class BufferInfo(
        val amplitude: Float = 0.0F,
        val peak: Float = 0.0F,
        val frequency: Float = 0.0F,
        val confidence: Float = 0.0F,
    )

    val TAG = "StreamAnalyzer"

    // user double buffering
    val bufferBack = FloatArray(bufferSize)
    var bufferFront = FloatArray(bufferSize)
    // Yin from tarsos DSP
    val detector = FastYin(44100F, bufferSize)
    var info = BufferInfo()
    var listener: (()->Unit)? = null

    var isRecording: Boolean = false
    val analysisDelay: Long = 500L

    private val streamScope = scope
    private lateinit var recordJob: Job
    private lateinit var analyzeJob: Job

    val manager: RecordingManager = RecordingManager()

    /**
     * Start threads that record and analyze
     * TODO: no wait analysis
     */
    fun startAnalyzing() {
        var i = 0
        val channel = Channel<FloatArray>()
        var ready: AtomicBoolean = AtomicBoolean(false)
        recordJob = streamScope.launch(Dispatchers.Default) {

            while (isActive) {
                /*
                The delay is mainly for debug,
                ideally there should not be any
                delay during regular operation
                 */
                delay(analysisDelay)
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
                listener?.invoke()

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

    fun analyzeBuffer(buffer: FloatArray) {
        var peak_amp = 0.0F
        var avg_amp = 0.0F

        for (i in buffer.indices) {
            var elem = buffer[i]*buffer[i]
            if (elem > peak_amp) peak_amp = elem
            avg_amp += elem
        }
        avg_amp = 2.0F*avg_amp/buffer.size

        val pitch = detector.getPitch(buffer)

        info = BufferInfo(avg_amp, peak_amp, pitch.pitch, pitch.probability)

        Log.d(TAG, "Peak: $peak_amp, Avg:  $avg_amp")
        Log.d(TAG, "Pitch: ${pitch.pitch}, Prob: ${pitch.probability}")
    }

    /*
    fun amplitudes(): Flow<Float> {

    }
    */
}
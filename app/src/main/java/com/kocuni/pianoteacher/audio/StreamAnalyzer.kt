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
class StreamAnalyzer(scope: CoroutineScope,
                     val bufferSize: Int = 1024) {

    data class BufferInfo(
        val amplitude: Float = 0.0F,
        val peak: Float = 0.0F,
        val frequency: Float = 0.0F,
        val confidence: Float = 0.0F,
    )

    private val TAG = "StreamAnalyzer"
    private val manager: RecordingManager = RecordingManager()

    // Yin from tarsos DSP
    val detector = FastYin(manager.SAMPLE_RATE.toFloat(), bufferSize)
    var info = BufferInfo()
    var listener: (()->Unit)? = null

    private val streamScope = scope
    private lateinit var recordJob: Job
    private lateinit var analyzeJob: Job

    /**
     * Start threads that record and analyze
     */
    fun startAnalyzing() {

        recordJob = streamScope.launch(Dispatchers.Default) {
            Log.d(TAG, "launched record job")
            manager.startStreaming()
            // this part is blocked
        }


        analyzeJob = streamScope.launch(Dispatchers.Default) {
            Log.d(TAG, "launched analyze job")
            while (isActive) {
                val buffer = manager.channel.receive()

                val floatArray = FloatArray(buffer.size)
                for (i in floatArray.indices) {
                    floatArray[i] = buffer[i]/32767.0F
                }

                analyzeBuffer(floatArray)
                listener?.invoke()

            }

        }
    }

    /**
     * Stop the threads
     */
    fun endAnalyzing() = runBlocking {
        manager.stopStreaming()
        recordJob.cancelAndJoin()
        analyzeJob.cancelAndJoin()
    }

    private fun analyzeBuffer(buffer: FloatArray) {
        var peak_amp = 0.0F
        var avg_amp = 0.0F

        for (i in buffer.indices) {
            val elem = buffer[i]*buffer[i]
            if (elem > peak_amp) peak_amp = elem
            avg_amp += elem
        }
        avg_amp = 2.0F*avg_amp/buffer.size

        val pitch = detector.getPitch(buffer)

        info = BufferInfo(avg_amp, peak_amp, pitch.pitch, pitch.probability)

        //Log.d(TAG, "Peak: $peak_amp, Avg:  $avg_amp")
        //Log.d(TAG, "Pitch: ${pitch.pitch}, Prob: ${pitch.probability}")
    }

}
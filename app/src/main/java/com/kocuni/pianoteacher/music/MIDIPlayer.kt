package com.kocuni.pianoteacher.music

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.billthefarmer.mididriver.MidiDriver

object MIDIPlayer {

    private val TAG = "MidiPlayer"

    private val event = ByteArray(3)

    private val midiDriver: MidiDriver = MidiDriver.getInstance()

    fun startDriver() {
        midiDriver.start()

        val config = midiDriver.config()

        Log.d(TAG, "maxVoices: " + config[0])
        Log.d(TAG, "numChannels: " + config[1])
        Log.d(TAG, "sampleRate: " + config[2])
        Log.d(TAG, "mixBufferSize: " + config[3])

    }

    fun stopDriver() {
        midiDriver.stop()
    }

    private fun startNote(midiCode: Int) {
        event[0] = (0x90 or 0x00).toByte()
        event[1] = midiCode.toByte()
        event[2] = 0x7F

        midiDriver.write(event)

    }

    private fun stopNote(midiCode: Int) {
        event[0] = (0x80 or 0x00).toByte()
        event[1] = midiCode.toByte()
        event[2] = 0x00

        midiDriver.write(event)
    }

    suspend fun testNote(midiCode: Int = 60, length: Long = 500L) {
        startNote(midiCode)
        delay(length)
        stopNote(midiCode)
    }

    fun playNote(context: CoroutineScope) {
        context.launch(Dispatchers.Default) {
            testNote()
        }
    }

}
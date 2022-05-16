package com.kocuni.pianoteacher.music

import android.util.Log
import kotlinx.coroutines.delay
import org.billthefarmer.mididriver.MidiDriver

class MIDIPlayer {

    private val TAG = "MidiPlayer"

    private val event = ByteArray(3)


    val midiDriver = MidiDriver.getInstance()

    init {
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

    fun startNote() {
        event[0] = (0x90 or 0x00).toByte()
        event[1] = 0x3C
        event[2] = 0x7F

        midiDriver.write(event)

    }

    fun stopNote() {
        event[0] = (0x80 or 0x00).toByte()
        event[1] = 0x3C
        event[2] = 0x00

        midiDriver.write(event)
    }

    suspend fun testNote() {
        startNote()
        delay(500L)
        stopNote()
    }

}
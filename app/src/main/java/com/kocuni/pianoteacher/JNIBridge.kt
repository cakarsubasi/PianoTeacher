package com.kocuni.pianoteacher

import android.util.Log

/**
 * Singleton class containing function definitions from native code
 */
object JNIBridge {

    private val TAG: String = "JNI Bridge"

    external fun stringFromJNI(): String
    external fun startEngine(): Boolean
    external fun stopEngine()
    external fun setRecording(isRecording: Boolean)
    external fun setPlaying(isPlaying: Boolean)
    external fun marshallTest(): Float

    /**
     * Load the native library
     */
    init {
        //System.loadLibrary("pianoteacher")
    }
}
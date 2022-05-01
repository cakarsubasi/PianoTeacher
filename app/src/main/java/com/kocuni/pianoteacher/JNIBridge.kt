package com.kocuni.pianoteacher

/**
 * Singleton class containing function definitions from native code
 */
object JNIBridge {

    external fun stringFromJNI(): String
    external fun startEngine(): Boolean
    external fun stopEngine()
    external fun setRecording(isRecording: Boolean)
    external fun setPlaying(isPlaying: Boolean)

    /**
     * Load the native library
     */
    init {
        System.loadLibrary("pianoteacher")
    }
}
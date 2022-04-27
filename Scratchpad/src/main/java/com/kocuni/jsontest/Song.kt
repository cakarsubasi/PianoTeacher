package com.kocuni.jsontest


class Song {
    /**
     * Ideas:
     * Song
     *   System
     *     Staff
     *     Measure
     *       Glyphs
     *   toStream
     *
     * Stream
     *   Chord
     *     Note
     *       pitch
     *       length (unimportant for now)
     *   nextMeasure
     *   prevMeasure
     *   nextChord
     *   prevChord
     *
     * Tutor
     *   'should hold a stream'
     *   'should be user controllable'
     *   'communicate with JNI'
     *   'likely in C++, detect dominant frequencies and amplitude'
     *   'if within sufficient margins, give positive callback'
     *   'otherwise give neutral or negative callback'
     *
     *   Cpp
     *   jni_bridge
     *   AudioEngine
     *   Callback class?
     *   Utils
     *     FT/Spectrogram
     */



    companion object {
        fun constructSong() {

        }
    }
}
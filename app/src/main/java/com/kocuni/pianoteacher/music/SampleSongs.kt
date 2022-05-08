package com.kocuni.pianoteacher.music

/**
 * Hardcoded sample songs
 * TODO(implement)
 */
object SampleSongs {

    fun song1() : Stream {



        val chordify = {str: String -> Stream.Chord(Stream.Note(str))}


        chordify("C4")



        return Stream(listOf())
    }
}
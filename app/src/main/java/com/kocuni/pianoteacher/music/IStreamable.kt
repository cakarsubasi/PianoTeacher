package com.kocuni.pianoteacher.music

interface IStreamable {

    fun currChord() : Stream.Chord?

    fun nextChord() : Stream.Chord?

    fun prevChord() : Stream.Chord?

    fun first() : Stream.Chord?

    fun last() : Stream.Chord?

}
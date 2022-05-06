package com.kocuni.pianoteacher.music

interface IStreamable {

    var idx: Int

    fun currChord() : Stream.Chord?

    fun nextChord() : Stream.Chord?

    fun prevChord() : Stream.Chord?

    fun first() : Stream.Chord?

    fun last() : Stream.Chord?

}
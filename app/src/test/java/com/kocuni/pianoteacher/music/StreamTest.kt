package com.kocuni.pianoteacher.music

import org.junit.Assert.*

import org.junit.Test

class StreamTest {

    fun generatePart() : Stream.Part {
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        return Stream.Part(listOf(note1, note2))
    }

    @Test
    fun currChord() {
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        val part1 = Stream.Part(listOf(note1, note2))
        val part2 = Stream.Part(listOf())
        val part3 = generatePart()
        assertEquals(part1.currChord(), note1)
        assertNull(part2.currChord())
        assertEquals(part3.currChord(), note1)
    }

    @Test
    fun nextChord() {
    }

    @Test
    fun prevChord() {
    }

    @Test
    fun nextPart() {
    }

    @Test
    fun prevPart() {
    }

    @Test
    fun currPart() {
    }

    @Test
    fun first() {
    }

    @Test
    fun last() {
    }
}
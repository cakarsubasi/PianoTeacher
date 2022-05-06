package com.kocuni.pianoteacher.music

import org.junit.Assert.*

import org.junit.Test

class StreamTest {

    private fun generatePart() : Stream.Part {
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        return Stream.Part(listOf(note1, note2))
    }

    @Test
    fun equals() {
        val note1 = Stream.Note("C4")
        val note2 = Stream.Note("C4")
        val note3 = Stream.Note("C4#")
        assertEquals(note1, note2)
        assertNotEquals(note1, note3)
        val chord1 = Stream.Chord(note1)
        val chord2 = Stream.Chord(note2)
        val chord3 = Stream.Chord(note3)
        val chord4 = chord1 + chord3
        assertEquals(chord1, chord2)
        assertNotEquals(chord1, chord3)
        assertNotEquals(chord1, note1)
        assertNotEquals(chord1, chord4)
        assertNotEquals(chord4, chord1)
    }

    @Test
    fun currChord() {
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        val part1 = Stream.Part(listOf(note1, note2))
        val part2 = Stream.Part(listOf()) // empty part
        val part3 = generatePart()
        assertEquals(part1.currChord(), note1)
        assertNull(part2.currChord())
        assertEquals(part3.currChord(), note1)

        val stream1 = Stream(listOf(part1))
        val stream2 = Stream(listOf(part1, part2, part3))
        val stream3 = Stream(listOf(part2)) // empty stream
        val stream4 = Stream(listOf(part2, part1)) // first part is empty but stream is not
        val stream5 = Stream(listOf(part2, part2)) // two empty parts
        val stream6 = Stream(listOf(stream5, stream2)) // beginning with a stream of two empty parts

        assertEquals(stream1.currChord(), note1)
        assertEquals(stream2.currChord(), note1)
        assertNull(stream3.currChord())
        assertEquals(stream4.currChord(), note1)
        assertNull(stream5.currChord())
        assertEquals(stream6.currChord(), note1)
    }

    @Test
    fun nextChord() {
        /**
         * Next chord internally calls first
         */
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        val part1 = Stream.Part(listOf()) // empty part
        val part2 = Stream.Part(listOf(note1))
        val part3 = Stream.Part(listOf(note1, note2, note1))

        assertNull(part1.nextChord())
        assertNull(part2.nextChord())
        assertEquals(part3.nextChord(), note2)
        assertEquals(part3.nextChord(), note1)
        assertNull(part3.nextChord())

        val stream1 = Stream(listOf(part3))
        assertEquals(stream1.nextChord(), note2)
        assertEquals(stream1.nextChord(), note1)
        assertNull(stream1.nextChord())

        val stream2 = Stream(listOf(stream1, stream1))
        assertEquals(stream2.nextChord(), note2)
        assertEquals(stream2.nextChord(), note1)
        assertEquals(stream2.nextChord(), note1)

        val stream3 = Stream(listOf(stream1, part1, stream2))
        assertEquals(stream3.nextChord(), note2)
        assertEquals(stream3.nextChord(), note1)
        assertEquals(stream3.nextChord(), note1)
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
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        val part1 = Stream.Part(listOf()) // empty part
        val stream1 = Stream(listOf(part1)) // empty stream

        assertNull(part1.first())
        assertNull(stream1.first())
    }

    @Test
    fun last() {
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        val part1 = Stream.Part(listOf()) // empty part
        val stream1 = Stream(listOf(part1)) // empty stream

        assertNull(part1.last())
        assertNull(stream1.last())

        val part2 = Stream.Part(listOf(note1))
        val part3 = Stream.Part(listOf(note1, note2))

        assertEquals(part2.last(), note1)
        assertEquals(part3.last(), note2)

        val stream2 = Stream(listOf(part2, part3))

        assertEquals(stream2.last(), note2)

        val stream3 = Stream(listOf(part2, part3, part1)) // empty part at the end
        assertEquals(stream3.last(), note2)

        val stream4 = Stream(listOf(stream3, stream1))
        assertEquals(stream4.last(), note2)
    }
}
package com.kocuni.pianoteacher.music

import com.kocuni.pianoteacher.music.data.SampleSongs
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
        assertEquals(stream1.currChord(), note1)

        val stream2 = Stream(listOf(part1, part2, part3))
        assertEquals(stream2.currChord(), note1)

        val stream3 = Stream(listOf(part2)) // empty stream
        assertNull(stream3.currChord())

        val stream4 = Stream(listOf(part2, part1)) // first part is empty but stream is not
        assertEquals(stream4.currChord(), note1)

        val stream5 = Stream(listOf(part2, part2)) // two empty parts
        assertNull(stream5.currChord())

        val stream6 = Stream(listOf(stream5, stream2)) // beginning with a stream of two empty parts
        assertEquals(stream6.currChord(), note1)

        val stream7 = Stream(listOf(stream5, part2, stream6))
        assertEquals(stream7.currChord(), note1)

    }

    @Test
    fun nextChord() {
        /**
         * Next chord internally calls first()
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

        val stream1 = Stream(listOf(part3)) // note1 note2 note1
        assertEquals(stream1.nextChord(), note2)
        assertEquals(stream1.nextChord(), note1)
        assertNull(stream1.nextChord())

        val stream2 = Stream(listOf(stream1, stream1)) // note1 note2 note1 note1 note2 note1
        assertEquals(stream2.nextChord(), note2)
        assertEquals(stream2.nextChord(), note1)
        assertEquals(stream2.nextChord(), note1)

        val stream3 = Stream(listOf(part3, part1, part3)) // n1 n2 n1 empty n1 n2 n1
        assertEquals(stream3.nextChord(), note2)
        assertEquals(stream3.nextChord(), note1)
        assertEquals(stream3.nextChord(), note1)
        assertEquals(stream3.nextChord(), note2)
        assertEquals(stream3.nextChord(), note1)
        assertNull(stream3.nextChord())

        // note1, note2, note1, empty part, note1, note2, note1
        val stream4 = Stream(listOf(stream1, part1, stream2))
        assertEquals(stream4.nextChord(), note2)
        assertEquals(stream4.nextChord(), note1)
        assertEquals(stream4.nextChord(), note1)
    }

    @Test
    fun prevChord() {
        /**
         * prevChord() internally calls last()
         */
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        val note3 = Stream.Chord(Stream.Note("E4"))
        val part1 = Stream.Part(listOf()) // empty part
        val part2 = Stream.Part(listOf(note2))
        val part3 = Stream.Part(listOf(note1, note2, note3)) // n1 n2 n3

        assertNull(part1.prevChord())
        assertNull(part2.prevChord())
        assertNull(part3.prevChord())

        part3.last()
        assertEquals(part3.prevChord(), note2)
        assertEquals(part3.prevChord(), note1)
        assertNull(part3.prevChord())

        val stream1 = Stream(listOf(part3, part1, part3)) // n1 n2 n3 e n1 n2 n3
        stream1.last()
        assertEquals(stream1.prevChord(), note2)
        assertEquals(stream1.prevChord(), note1)
        assertEquals(stream1.prevChord(), note3)
        assertEquals(stream1.prevChord(), note2)
        assertEquals(stream1.prevChord(), note1)
        assertNull(stream1.prevChord())

        val stream2 = Stream(listOf(stream1, part1, part2, part1)) // n1 n2 n3 e n1 n2 n3 e n1 e
        stream2.last()
        assertEquals(stream2.prevChord(), note3)
        assertEquals(stream2.prevChord(), note2)
        assertEquals(stream2.prevChord(), note1)
        assertEquals(stream2.prevChord(), note3)
        assertEquals(stream2.prevChord(), note2)
        assertEquals(stream2.prevChord(), note1)
        assertNull(stream2.prevChord())

    }

    @Test
    fun nextPartChord() {
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        val note3 = Stream.Chord(Stream.Note("E4"))

        val part1 = Stream.Part(listOf())
        val part2 = Stream.Part(listOf(note1))

        val stream1 = Stream(listOf(part1)) // e
        val stream2 = Stream(listOf(part1, part2)) // e n1
        val stream3 = Stream(listOf(part2, part1)) // n1 e

        assertNull(stream1.nextPartChord())
        assertNull(stream3.nextPartChord())
        assertEquals(stream2.nextPartChord(), note1)
        assertNull(stream2.nextPartChord())

        val part3 = Stream.Part(listOf(note2))
        val part4 = Stream.Part(listOf(note3))

        val stream4 = Stream(listOf(part2, part3, part4))
        val stream5 = Stream(listOf(stream4, stream1, stream4)) // n1 n2 n3 e n1 n2 n3
        assertEquals(stream5.nextPartChord(), note2)
        assertEquals(stream5.nextPartChord(), note3)
        assertEquals(stream5.nextPartChord(), note1)
        assertEquals(stream5.nextPartChord(), note2)
        assertEquals(stream5.nextPartChord(), note3)
        assertNull(stream5.nextPartChord())
        assertEquals(stream5.prevPartChord(), note3)
    }

    @Test
    fun nextSegment() {
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        val note3 = Stream.Chord(Stream.Note("E4"))

        val part1 = Stream.Part(listOf())

        val stream1 = Stream(listOf())
        val stream2 = Stream(listOf(part1))
        val stream3 = Stream(listOf(part1, part1))
        val stream4 = Stream(listOf(stream2, stream1, part1))

        assertNull(stream1.nextSegment())
        assertNull(stream1.nextSegment())
        assertNull(stream2.nextSegment())
        assertNull(stream2.nextSegment())
        assertNull(stream3.nextSegment())
        assertNull(stream3.nextSegment())
        assertNull(stream3.nextSegment())
        assertNull(stream4.nextSegment())
        assertNull(stream4.nextSegment())
        assertNull(stream4.nextSegment())
        assertNull(stream4.nextSegment())

        val part2 = Stream.Part(listOf(note1, note2, note3))
        val part3 = Stream.Part(listOf(note2, note1, note3))
        val part4 = Stream.Part(listOf(note3, note1, note2))

        val stream5 = Stream(listOf(part2, part3, part4))
        assertEquals(stream5.nextSegment(), note2)
        assertEquals(stream5.nextSegment(), note3)
        assertNull(stream5.nextSegment())

        val stream6 = Stream(listOf(part1, part2)) // e n1 n2 n3
        assertEquals(stream6.nextSegment(), note1) // empty parts are still valid parts!
        assertNull(stream6.nextSegment())

        val stream7 = Stream(listOf(stream6, stream6)) //
        assertEquals(stream7.nextSegment(), note1)
        assertNull(stream7.nextSegment())
        assertNull(stream7.nextSegment())
    }

    @Test
    fun prevPartChord() {
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        val note3 = Stream.Chord(Stream.Note("E4"))

        val part1 = Stream.Part(listOf())
        val part2 = Stream.Part(listOf(note1))

        val stream1 = Stream(listOf(part1)) // e
        val stream2 = Stream(listOf(part1, part2)) // e n1
        val stream3 = Stream(listOf(part2, part1)) // n1 e

        assertNull(stream1.prevPartChord())
        stream3.last()
        assertNull(stream3.prevPartChord())
        stream2.last()
        assertNull(stream2.prevPartChord())

        val part3 = Stream.Part(listOf(note2))
        val part4 = Stream.Part(listOf(note3))

        val stream4 = Stream(listOf(part2, part3, part4))
        val stream5 = Stream(listOf(stream4, stream1, stream4)) // n1 n2 n3 e n1 n2 n3
        stream5.last()
        assertEquals(stream5.prevPartChord(), note2)
        assertEquals(stream5.prevPartChord(), note1)
        assertEquals(stream5.prevPartChord(), note3)
        assertEquals(stream5.prevPartChord(), note2)
        assertEquals(stream5.prevPartChord(), note1)
        assertNull(stream5.prevPartChord())
        assertEquals(stream5.nextPartChord(), note1)

        val part5 = Stream.Part(listOf(note1, note2, note3))
        val stream6 = Stream(listOf(part5))
        assertNull(stream6.nextPartChord())
        assertEquals(stream6.prevPartChord(), note1)
        val stream7 = Stream(listOf(stream6, part5, stream6)) // {{n1, n2, n3}}, {n1, n2, n3}, {{n1, n2, n3}}
        assertNull(stream7.prevPartChord())
        assertEquals(stream7.nextPartChord(), note1)
        assertEquals(stream7.nextPartChord(), note1)
        assertEquals(stream7.nextPartChord(), note1)
        assertNull(stream7.nextPartChord())
        assertEquals(stream7.prevPartChord(), note1)
        assertEquals(stream7.prevPartChord(), note1)
        assertEquals(stream7.prevPartChord(), note1)
    }

    @Test
    fun prevSegment() {
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        val note3 = Stream.Chord(Stream.Note("E4"))

        val part1 = Stream.Part(listOf())

        val stream1 = Stream(listOf())
        val stream2 = Stream(listOf(part1))
        val stream3 = Stream(listOf(stream2, stream1, part1))

        assertNull(stream1.prevSegment())
        assertNull(stream1.prevSegment())
        assertNull(stream2.prevSegment())
        assertNull(stream2.prevSegment())
        stream3.last()
        assertNull(stream3.prevSegment())
        assertNull(stream3.prevSegment())
        assertNull(stream3.prevSegment())

        val part2 = Stream.Part(listOf(note1, note2, note3))
        val part3 = Stream.Part(listOf(note2, note1, note3))
        val part4 = Stream.Part(listOf(note3, note1, note2))

        val stream4 = Stream(listOf(part2, part3, part4)) // n1 n2 n3
        stream4.last()
        assertEquals(stream4.prevSegment(), note2)
        assertEquals(stream4.prevSegment(), note1)
        assertNull(stream4.prevSegment())

        val stream5 = Stream(listOf(part1, part2)) // e n1 n2 n3
        stream5.nextSegment()
        stream5.nextSegment()
        assertEquals(stream5.prevSegment(), note1) // empty parts are still valid parts!
        assertNull(stream5.prevSegment())

    }

    @Test
    fun currPartChord() {
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        val note3 = Stream.Chord(Stream.Note("E4"))

        val part1 = Stream.Part(listOf())
        val part2 = Stream.Part(listOf(note1))
        val part3 = Stream.Part(listOf(note1, note2, note3))
        val part4 = Stream.Part(listOf(note2, note3, note1))
        val part5 = Stream.Part(listOf(note3, note2, note1))

        val stream1 = Stream(listOf())
        val stream2 = Stream(listOf(part1))

        assertNull(stream1.currPartChord())
        assertNull(stream2.currPartChord())
        stream1.nextPartChord()
        assertNull(stream1.currPartChord())
        stream2.nextPartChord()
        assertNull(stream2.currPartChord())

        val stream3 = Stream(listOf(part1, part2)) // e, n1
        assertNull(stream3.currPartChord())
        stream3.last()
        assertEquals(stream3.currPartChord(), note1)
        stream3.nextPartChord()
        assertNull(stream3.currPartChord())
        stream3.prevPartChord()
        assertEquals(stream3.currPartChord(), note1)

        val stream4 = Stream(listOf(part3, part4, part5))
        assertEquals(stream4.currPartChord(), note1)
        stream4.nextPartChord()
        assertEquals(stream4.currPartChord(), note2)
        stream4.nextPartChord()
        assertEquals(stream4.currPartChord(), note3)

        val stream5 = Stream(listOf(stream3, part1, stream3)) // e, n1, e, e, n1
        assertEquals(stream5.currPartChord(), note1) // empty parts that are wrapped are inaccessible
        stream5.nextPartChord()
        assertEquals(stream5.currPartChord(), note1) // nextPart() skips empty parts
        stream5.nextPartChord()
        assertNull(stream5.currPartChord())
    }

    @Test
    fun first() {
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        val part1 = Stream.Part(listOf()) // empty part
        val stream1 = Stream(listOf(part1)) // empty stream

        assertNull(part1.first())
        assertNull(stream1.first())

        val part2 = Stream.Part(listOf(note1, note2))
        assertEquals(part2.first(), note1)
        part2.nextChord()
        assertEquals(part2.first(), note1)

        val stream2 = Stream(listOf(part2))
        assertEquals(stream2.first(), note1)
        stream2.nextChord()
        assertEquals(stream2.first(), note1)

        val stream3 = Stream(listOf(part1, part2)) // empty part, then note 1
        assertEquals(stream3.first(), note1)
        stream3.nextChord()
        assertEquals(stream3.first(), note1)

        val stream4 = Stream(listOf(stream1, part2)) // empty stream, then note 1
        assertEquals(stream4.first(), note1)
        stream4.nextChord()
        assertEquals(stream4.first(), note1)
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

    @Test
    fun combinedTest() {
        val chordify = {str: String -> Stream.Chord(Stream.Note(str))}

        val generatePart = { str: String ->
            val list: MutableList<Stream.Chord> = mutableListOf()
            str.splitToSequence(" ").forEach {
                list.add(chordify(it))
            }
            Stream.Part(list)
        }

        val str = "C4 D4 E4"



        val stream = Stream(listOf(generatePart(str)))

        println(stream)


    }

    @Test
    fun get() {
        val note1 = Stream.Chord(Stream.Note("C4"))
        val note2 = Stream.Chord(Stream.Note("D4"))
        val note3 = Stream.Chord(Stream.Note("E4"))

        val part1 = Stream.Part(listOf())
        val part2 = Stream.Part(listOf(note1))
        val part3 = Stream.Part(listOf(note1, note2, note3))
        //val part4 = Stream.Part(listOf(note2, note3, note1))
        //val part5 = Stream.Part(listOf(note3, note2, note1))

        assertEquals(part1.size, 0)
        assertEquals(part2.size, 1)
        assertEquals(part3.size, 3)

        assertEquals(part3[0], note1)
        assertEquals(part3[1], note2)
        assertEquals(part3[2], note3)

    }

    @Test
    fun currPart() {

    }

    /**
     * Not real tests but probably good enough
     */
    @Test
    fun flattenStream() {
        val stream1 = SampleSongs.song1()
        val stream2 = StreamBuilder.flattenStream(stream1)

        assertEquals(stream1.toString(), stream2.toString())

        val stream3 = Stream(listOf(stream1, stream2))
        val stream4 = StreamBuilder.flattenStream(stream3)
        assertNotEquals(stream3.toString(), stream4.toString())

    }
}
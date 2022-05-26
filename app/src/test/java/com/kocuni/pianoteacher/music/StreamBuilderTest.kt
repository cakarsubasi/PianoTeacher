package com.kocuni.pianoteacher.music

import org.junit.Assert.*

import org.junit.Test

/**
 * Tests the Builder singleton and validates its inputs
 */
class StreamBuilderTest {

    fun song1(isOneHanded: Boolean) : AbstractSong {
        val topStaff = Staff(0.3, 0.4)
        val bottomStaff = Staff(0.6, 0.7)

        val generateNotes = { n: Int, left: Double, right: Double, bot: Double, top: Double ->
            List<Glyph>(n) {
                GlyphNote(left+(right-left)/(n+1), bot-(bot-top)/(n+1), 0.0, 0.0)
            }
        }

        val measure1 = Measure(
            generateNotes(4, 0.1, 0.3, 0.4, 0.3)
        )

        val system1 = SystemStaff().also{
            it.staffs = listOf(topStaff)
            it.ymax = 0.5; it.ymin = 0.0
            it.measures = listOf(measure1)
        }
        val system2 = SystemStaff().also{
            it.staffs = listOf(bottomStaff)
            it.ymax = 1.0; it.ymin = 0.5
            it.measures = listOf()
        }

        val abstractSong = AbstractSong().also {
            it.staffs = listOf(system1, system2)
            it.isOneHanded = isOneHanded
        }

        return abstractSong
    }


    @Test
    fun build() {
        val abstractSong = song1(isOneHanded = true)

        val stream1 = StreamBuilder.build(abstractSong)

        assertNotNull(stream1)
        assertNotNull(stream1.currChord())
        assertNotNull(stream1.nextChord())
        assertNotNull(stream1.nextChord())
        assertNotNull(stream1.nextChord())
        assertNull(stream1.nextChord())


    }
}
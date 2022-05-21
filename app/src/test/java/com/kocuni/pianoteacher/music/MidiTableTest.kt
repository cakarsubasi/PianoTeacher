package com.kocuni.pianoteacher.music

import com.kocuni.pianoteacher.music.data.MidiTable
import org.junit.Assert.*

import org.junit.Test

class MidiTableTest {

    @Test
    fun getFrequency() {
        val A4 = MidiTable.table["A4"]
        val A3 = MidiTable.table["A3"]
        val A5 = MidiTable.table["A5"]
        val upper = 1.05
        val lower = 0.96
        assertNotNull(A4)
        if (A4 != null) {
            assert(
                MidiTable.getFrequency(A4) > 440.0*lower &&
                    MidiTable.getFrequency(A4) < 440.0*upper)
        }
        assertNotNull(A3)
        if (A3 != null) {
            assert(
                MidiTable.getFrequency(A3) > 220.0*lower &&
                    MidiTable.getFrequency(A3) < 220.0*upper)
        }
        assertNotNull(A5)
        if (A5 != null) {
            assert(
                MidiTable.getFrequency(A5) > 880.0*lower &&
                    MidiTable.getFrequency(A5) < 880.0*upper)
        }
    }
}
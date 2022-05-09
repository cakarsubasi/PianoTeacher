package com.kocuni.pianoteacher.music

import kotlin.math.pow

object MidiTable {

    val table: HashMap<String, Int> = HashMap<String, Int>().also {
        for (i in 0..10) {
            it["C$i"] = 12*i
            it["C$i#"] = 12*i + 1
            it["D$i"] = 12*i + 2
            it["D$i#"] = 12*i + 3
            it["E$i"] = 12*i + 4
            it["F$i"] = 12*i + 5
            it["F$i#"] = 12*i + 6
            it["G$i"] = 12*i + 7
            it["G$i#"] = 12*i + 8
            it["A$i"] = 12*i + 9
            it["A$i#"] = 12*i + 10
            it["B$i"] = 12*i + 11
        } // last four entries are invalid, but that is not important
    }

    fun getFrequency(midiCode: Int): Double {
        // A4 (57) is 440.0
        val f0 = 440.0
        val a = 2.0.pow(1.0 / 12.0)
        return f0 * a.pow(midiCode - 57)
    }

}
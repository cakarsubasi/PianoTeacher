package com.kocuni.pianoteacher.music.data

import kotlin.math.pow

object MidiTable {

    val table: HashMap<String, Int> = HashMap<String, Int>().also {
        for (i in -1..10) {
            it["C$i"]  = 12*(i+1)
            it["C$i#"] = 12*(i+1) + 1
            it["D$i"]  = 12*(i+1) + 2
            it["D$i#"] = 12*(i+1) + 3
            it["E$i"]  = 12*(i+1) + 4
            it["F$i"]  = 12*(i+1) + 5
            it["F$i#"] = 12*(i+1) + 6
            it["G$i"]  = 12*(i+1) + 7
            it["G$i#"] = 12*(i+1) + 8
            it["A$i"]  = 12*(i+1) + 9
            it["A$i#"] = 12*(i+1) + 10
            it["B$i"]  = 12*(i+1) + 11
        } // last four entries are invalid, but that is not important
    }

    val midiToKey = mutableMapOf<Int, String>().also {
        table.forEach { (k, v) -> it[v] = k }
    }

    fun getFrequency(midiCode: Int): Double {
        // A4 (57) is 440.0
        val f0 = 440.0
        val a = 2.0.pow(1.0 / 12.0)
        return f0 * a.pow(midiCode - 57)
    }

}
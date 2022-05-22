package com.kocuni.pianoteacher.music.data

object ClefMaps {

    val clefs = { name: String ->
        when (name) {
            "gClef" -> gclefmap
            "fClef" -> fclefmap
            "cClef" -> cclefmap
            else -> null
        }
    }

    val gclefmap = HashMap<Int, String>().also {
        it[-4] = "A3"
        it[-3] = "B3"
        it[-2] = "C4"
        it[-1] = "D4"
        it[0]  = "E4"
        it[1]  = "F4"
        it[2]  = "G4"
        it[3]  = "A4"
        it[4]  = "B4"
        it[5]  = "C5"
        it[6]  = "D5"
        it[7]  = "E5"
        it[8]  = "F5"
        it[9]  = "G5"
        it[10]  = "A5"
        it[11]  = "B5"
        it[12]  = "C6"
    }

    val fclefmap = HashMap<Int, String>().also {
        it[-4] = "C2"
        it[-3] = "D2"
        it[-2] = "E2"
        it[-1] = "F2"
        it[0]  = "G2"
        it[1]  = "A2"
        it[2]  = "B2"
        it[3]  = "C3"
        it[4]  = "D3"
        it[5]  = "E3"
        it[6]  = "F3"
        it[7]  = "G3"
        it[8]  = "A3"
        it[9]  = "B3"
        it[10]  = "C4"
        it[11]  = "D4"
        it[12]  = "E4"
    }

    /**
     * There are technically two common versions of the
     * C-clef. This assumes the Alto clef as opposed to the Tenor clef
     */
    val cclefmap = HashMap<Int, String>().also {
        it[-4] = "B2"
        it[-3] = "C3"
        it[-2] = "D3"
        it[-1] = "E3"
        it[0]  = "F3"
        it[1]  = "G3"
        it[2]  = "A3"
        it[3]  = "B3"
        it[4]  = "C4"
        it[5]  = "D4"
        it[6]  = "E4"
        it[7]  = "F4"
        it[8]  = "G4"
        it[9]  = "A4"
        it[10]  = "B4"
        it[11]  = "C5"
        it[12]  = "D5"
    }
}
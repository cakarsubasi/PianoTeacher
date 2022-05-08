package com.kocuni.pianoteacher.music

object ClefMaps {

    val clefs = HashMap<String, HashMap<Int, String>>().also {
        it["gClef"] = gclefmap
        it["fClef"] = fclefmap
        it["cClef"] = cclefmap
    }

    val gclefmap = HashMap<Int, String>().also {
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
        // TODO: Insert the correct note names
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

    val cclefmap = HashMap<Int, String>().also {
        // TODO: Insert the correct note names
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
}
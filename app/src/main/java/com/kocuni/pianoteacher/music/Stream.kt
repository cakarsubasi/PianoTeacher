package com.kocuni.pianoteacher.music

import com.kocuni.pianoteacher.music.AbstractSong
import com.kocuni.pianoteacher.music.Chord
import com.kocuni.pianoteacher.music.GlyphNote
import java.util.*
import kotlin.math.roundToInt

class Stream(abstractSong: AbstractSong) {
    val stream = TreeMap<Int, Chord>()
    /**
     * Traverse each measure in the song, generate chords and measures
     *
     * One handed case first
     */
    init {

    }

    /**
     * TODO
     * Return the current chord
     */
    fun getCurrentChord() {

    }

    /**
     * TODO
     * Return the part which holds the current chord
     */
    fun getCurrentPart() {

    }

    /**
     * TODO
     * Move the pointer to the next chord
     */
    fun nextChord() {

    }

    /**
     * TODO
     * Move the pointer to the previous chord
     */
    fun prevChord() {

    }

    /**
     * TODO
     * Move the pointer to the first chord in the next
     * measure
     */
    fun nextPart() {

    }

    /**
     * TODO
     * Move the pointer to the first chord in the previous
     * measure
     */
    fun prevPart() {

    }

    /**
     * TODO
     * Move the pointer to the first chord in the current measure
     */
    fun currPart() {

    }

    /**
     * TODO
     * Move the pointer to the first chord
     */
    fun first() {

    }

    override fun toString(): String {
        return super.toString()
    }

    object Builder {

        fun build(abstractSong: AbstractSong): Stream {

            for (staff in abstractSong.staffs) {
                val gap = (staff.ymax - staff.ymin)*0.25
                for (measure in staff.measures) {


                    for (glyph in measure.glyphs) {
                        if (glyph is GlyphNote) {
                            // infer pitch add
                            val pos = relativePos(glyph.y, staff.ymax, gap)
                            val noteName: String? = gclefmap[pos]
                            val chord: Chord
                            val note: Note
                            if (noteName != null) {
                                note = Note(glyph, noteName)

                            } else {
                                println("Note out of bounds at $pos")
                                note = Note(glyph, "Unknown")
                            }
                            chord = Chord(note)

                        }
                    }
                }
            }
            return Stream(abstractSong)
        }

        val relativePos = { notehead:    Double,
                            staffbottom: Double,
                            staffgap:    Double ->
            (2.0*(staffbottom - notehead)/staffgap).roundToInt()
        }

        val getNote = {}

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

        fun getPitch(name: String) : Double {
            // TODO, either add a LUT or a formula to calculate pitch
            return 0.0
        }
    }
}

fun main() {


}
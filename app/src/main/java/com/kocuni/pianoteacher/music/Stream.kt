package com.kocuni.pianoteacher.music

import android.util.Log
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt

/**
 * A stream is a section of a song larger than a measure
 * It can be a line, a page, or the whole song. It
 * is constructed recursively so there can be many levels
 */
class Stream(var stream: List<IStreamable>) : IStreamable {
    override var idx = 0
    /**
     * Traverse each measure in the song, generate chords and measures
     *
     * One handed case first
     */
    init {
        /**
         * When a new stream is constructed, reset the internal
         * state of every part or stream inside for more consistent behavior
         */
        for (s in stream) {
            s.first()
        }
    }

    /**
     * Return the current chord
     *
     * @return Chord the index is pointing to
     * null if the stream is empty or if the pointer is ahead of the beginning
     * of the stream or beyond the end of the stream
     */
    override fun currChord(): Chord? {
        return if (stream.isEmpty()) {
            null
        } else {
            if (idx >= stream.size) {
                return null
            } else {
                val chord = stream[idx].currChord()
                if (chord == null) { // empty part
                    idx++
                    return currChord()
                } else {
                    return chord
                }
            }
        }
    }

    /**
     * Return the part which holds the current chord
     */
    fun getCurrentPart() : IStreamable {
        return stream[idx]
    }

    /**
     * Move the pointer to the next chord,
     *
     * This method mutates the stream.
     *
     * @return newly pointed upon chord.
     * null if stream is empty or the chord was at the end of the stream prior to the call
     */
    override fun nextChord(): Chord? {
        return if (idx == stream.size) {
            null // end of the stream
        } else {
            var next = stream[idx].nextChord()
            while (next == null) {
                ++idx
                if (idx >= stream.size) { // end of the stream
                    return null
                } else {
                    next = stream[idx].first()
                }
            }
            return next
        }
    }

    /**
     * Move the pointer to the previous chord
     *
     * This method mutates the stream.
     *
     * @return newly pointed upon chord.
     * null if stream is empty or the chord was at the beginning of the stream prior to the call
     */
    override fun prevChord(): Chord? {
        return if (idx == -1) {
            null // end of the stream
        } else {
            var prev = stream[idx].prevChord()
            while (prev == null) {
                --idx
                if (idx < 0) { // out of bounds
                    return null
                } else {
                    prev = stream[idx].last()
                }
            }
            return prev
        }
    }

    /**
     * Move the pointer to the first chord in the next
     * nonempty measure
     */
    fun nextPart() : Chord? {
        return if (stream.isEmpty()) {
            null
        } else {
            val st = stream[idx]
            if (st is Part) {
                st.last()
                nextChord()
            } else if (st is Stream) {
                st.nextPart() ?: nextChord()
            } else {
                null
            }
        }

    }

    /**
     * Moves the pointer to the first chord in the next element in
     * the top most stream. If this stream only contains Parts,
     * this method is semantically equivalent to nextPart()
     */
    fun nextSegment() : Chord? {
        ++idx
        return if (idx >= stream.size) {
            null
        } else {
            stream[idx].first()
        }
    }

    /**
     * Move the pointer to the first chord in the previous
     * section (TODO)
     */
    fun prevPart() : Chord? {
        --idx
        return if (idx == -1) {
            null
        } else {
            stream[idx].first()
        }
    }

    /**
     * Moves the pointer to the first chord in the previous element in
     * the top most stream. If this stream only contains Parts,
     * this method is semantically equivalent to prevPart()
     */
    fun prevSegment() : Chord? {
        --idx
        return if (idx == -1) {
            null
        } else {
            stream[idx].first()
        }
    }

    /**
     * Move the pointer to the first chord in the current measure
     */
    fun currPart() : Chord? {
        return if (idx == -1 && idx >= stream.size) {
            null
        } else {
            stream[idx].first()
        }

    }

    /**
     * Move the pointer to the first chord
     */
    override fun first(): Chord? {
        idx = 0
        return if (stream.isEmpty()) {
            null
        } else {
            val chord = stream[idx].first()
            if (chord == null) {
                nextChord()
            } else {
                chord
            }
        }
    }

    /**
     * Move the pointer to the last chord
     */
    override fun last(): Chord? {
        idx = stream.size - 1
        return if (stream.isEmpty()) {
            null
        } else {
            val chord = stream[idx].last()
            if (chord == null) {
                prevChord()
            } else {
                chord
            }
        }
    }

    override fun toString(): String {
        var str = "--\n"
        for (s in stream) {
            str += "${s}\n"
        }
        return "$str--\n"
    }

    /**
     * A part is essentially a measure
     */
    class Part(var chords: List<Chord>) : IStreamable {

        override var idx: Int = 0

        override fun currChord(): Chord? {
            return if (chords.isEmpty()) {
                null
            } else if (idx >= chords.size || idx < 0) {
                null
            } else {
                chords[idx]
            }
        }

        override fun nextChord() : Chord? {
            ++idx
            return if (idx >= chords.size) {
                null // call first() on next Part
            } else {
                chords[idx]
            }
        }

        override fun prevChord() : Chord? {
            --idx
            return if (idx <= -1) {
                null // call last() on next Part
            } else {
                chords[idx]
            }
        }

        override fun first() : Chord? {
            idx = 0
            return if (chords.isEmpty()) {
                null // empty case
            } else {
                chords[idx]
            }
        }

        override fun last() : Chord? {
            idx = chords.size - 1
            return if (chords.isEmpty()) {
                null // empty case
            } else {
                chords[idx]
            }
        }

        override fun toString() : String {
            var str = String()
            for (chord in chords) {
                str += "$chord "
            }
            return str
        }
    }

    /**
     * A list of notes that must be played at
     * the same time.
     */
    class Chord() {
        val notes: ArrayList<Note> = ArrayList()
        var timeIndex: Double = 0.0

        constructor(note: Note) : this() {
            this.notes.add(note)
            this.timeIndex = note.glyph.x
        }

        // merge the chords
        operator fun plus(o: Chord) : Chord {
            val chord = Chord()
            chord.notes.addAll(this.notes)
            chord.notes.addAll(o.notes)
            chord.timeIndex = (this.timeIndex + o.timeIndex)/2.0
            return chord
        }

        override fun equals(other: Any?): Boolean {
            return if (other is Chord) {
                notes.containsAll(other.notes) and other.notes.containsAll(this.notes)
            } else {
                super.equals(other)
            }
        }

        override fun toString() : String {
            var str = ""
            for (note in notes) {
                str += note.toString()
            }
            return str
        }

        override fun hashCode(): Int {
            var hash = 1
            for (note in notes) {
                hash *= note.hashCode() // usually unique
            }
            return hash
        }

    }

    class Note() {
        constructor(glyph: GlyphNote, name: String) : this(name) {
            this.glyph = glyph
        }
        constructor(name: String) : this() {
            this.name = name
        }

        var pitch: Double = 0.0
        var name: String = "C4"
        var glyph: GlyphNote = GlyphNote()

        override fun equals(other: Any?): Boolean {
            return if (other is Note)  {
                this.name == other.name
            } else {
                super.equals(other) // always false
            }

        }

        override fun toString() : String {
            return this.name
        }

        override fun hashCode(): Int {
            val code = MidiTable.table[this.name]
            return if (code == null) {
                0 // represents an invalid value
            } else {
                code + 1
            }

        }
    }

    object Builder {
        private val TAG = "StreamFactory"

        fun build(abstractSong: AbstractSong): Stream {
            val staffs = LinkedList<IStreamable>()
            for (abstractStaff in abstractSong.staffs) {
                val gap = (abstractStaff.ymax - abstractStaff.ymin) * 0.25

                val measures = LinkedList<IStreamable>()
                for (abstractMeasure in abstractStaff.measures) {

                    val chords = LinkedList<Chord>()
                    for (glyph in abstractMeasure.glyphs) {
                        if (glyph is GlyphNote) {
                            // infer pitch add
                            val pos = relativePos(glyph.y, abstractStaff.ymax, gap)
                            // TODO: consider other clefs
                            val noteName: String? = gclefmap[pos]
                            val chord: Chord
                            val note: Note
                            if (noteName != null) {
                                note = Note(glyph, noteName)

                            } else {
                                Log.d(TAG, "Note out of bounds at $pos")
                                note = Note(glyph, "Unknown")
                            }
                            chord = Chord(note)
                            chords.add(chord)
                        }
                    }
                    val part = Part(chords)
                    measures.add(part)
                }
                val staff = Stream(measures)
                staffs.add(staff)
            }
            return Stream(staffs)
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
package com.kocuni.pianoteacher.music

import android.util.Log
import com.kocuni.pianoteacher.music.data.ClefMaps
import com.kocuni.pianoteacher.music.data.MidiTable
import com.kocuni.pianoteacher.music.data.TutorableSong
import com.kocuni.pianoteacher.music.data.Voices
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
    var size = stream.size
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

    operator fun get(i: Int) : IStreamable {
        return stream[i]
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
     * Return the strean which holds the current chord
     */
    fun getCurrentPart() : IStreamable {
        return stream[idx]
    }

    fun currPart() : Part? {
        val part: IStreamable = stream[idx]
        if (part is Part) {
            return part
        } else if (part is Stream){
            return part.currPart()
        }
        return null
    }

    /**
     * Lazy abomination of an implementation
     */
    fun nextPart() : Part? {
        nextPartChord()
        val ret = currPart()
        prevPartChord()
        return ret
    }

    /**
     * Lazy abomination of an implementation
     */
    fun prevPart() : Part? {
        prevPartChord()
        val ret = currPart()
        nextPartChord()
        return ret
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
    fun nextPartChord() : Chord? {
        return if (stream.isEmpty()) {
            null
        } else {
            if (idx < 0) {
                return first()
            }
            when (val st = stream[idx]) {
                is Part -> {st.last(); return nextChord()}
                is Stream -> return st.nextPartChord() ?: nextChord()
                else -> return null
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
     * nonempty measure
     *
     * @return first chord in the measure previous to the last
     * measure that was pointed to. null if the pointer was
     * pointing at the first measure. If the pointer is out of
     * bounds due to nextPart() calls, the first chord in the last measure
     */
    fun prevPartChord() : Chord? {
        return if (stream.isEmpty()) {
            null
        } else {
            if (idx >= stream.size) {
                last()
                return currPartChord()
            }
            if (idx < 0) {
                return null
            }
            when (val st = stream[idx]) {
                is Part -> {
                    if (prevChord() != null) {
                        return currPartChord()
                    } else {
                        return null
                    }
                }
                is Stream -> {
                    val st2 = st.prevPartChord()
                    if (st2 == null) {
                        prevChord()
                        return currPartChord()
                    } else {
                        return st2
                    }
                }
                else -> return null
            }
        }
    }

    /**
     * Moves the pointer to the first chord in the previous element in
     * the top most stream. If this stream only contains Parts,
     * this method is semantically equivalent to prevPart()
     */
    fun prevSegment() : Chord? {
        --idx
        return if (idx <= -1) {
            null
        } else {
            stream[idx].first()
        }
    }

    /**
     * Move the pointer to the first chord in the current measure
     *
     * @return the first chord in the current measure.
     * null if current part is out of bounds. null if pointing at an empty measure under select circumstances.
     */
    fun currPartChord() : Chord? {
        return if (stream.isEmpty()) {
            null
        } else {
            if (idx >= stream.size) {
                return null
            } else if (idx < 0) {
                return null
            }
            when (val st = stream[idx]) {
                is Part -> st.first()
                is Stream -> st.currPartChord() ?: nextChord()
                else -> null
            }

        }
    }

    /**
     * Move the pointer to the first chord
     *
     * @return the first chord in the stream.
     * null if stream is empty
     */
    override fun first(): Chord? {
        idx = 0
        return if (stream.isEmpty()) {
            null
        } else {
            stream[idx].first() ?: nextChord()
        }
    }

    /**
     * Move the pointer to the last chord
     *
     * @return the last chord in the stream.
     * null if stream is empty
     */
    override fun last(): Chord? {
        idx = stream.size - 1
        return if (stream.isEmpty()) {
            null
        } else {
            stream[idx].last() ?: prevChord()
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
        var size = chords.size

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

        operator fun get(i: Int) : Chord {
            return chords[i]
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
        private const val TAG = "StreamBuilder"

        /**
         * Use this to construct songs for the Song Tutor from inference results.
         */
        fun buildTutorable(abstractSong: AbstractSong) : TutorableSong {
            val voices = mutableListOf<Voices>(Voices.SOPRANO)
            if (!abstractSong.isOneHanded) {
                voices.add(Voices.TENOR)
            }
            val rawStream = build(abstractSong = abstractSong)
            return TutorableSong(rawStream, voices)
        }

        fun build(abstractSong: AbstractSong) : Stream {
            return if (abstractSong.isOneHanded) { // one handed
                val clef = "gClef"
                val stream = buildSingleStream(abstractSong.staffs, clef)
                stream
            } else { // two handed
                val clef1 = "gClef"
                val clef2 = "fClef"
                // split staffs
                val staffsR = abstractSong.staffs.filterIndexed { index, _ ->
                    index % 2 == 0
                }
                val staffsL = abstractSong.staffs.filterIndexed { index, _ ->
                    index % 2 == 1
                }
                val streamR = buildSingleStream(staffsR, clef1)
                val streamL = buildSingleStream(staffsL, clef2)
                Stream(listOf(streamR, streamL)) // stream is not private and easily accessible!
            }
        }

        private fun buildSingleStream(abstractStaffs: List<SystemStaff>, clef: String) : Stream {
            // determine which mapping will be used
            val clefmap: HashMap<Int, String> = ClefMaps.clefs[clef] ?: ClefMaps.gclefmap

            val staffs = LinkedList<IStreamable>()
            for (abstractStaff in abstractStaffs) {
                val gap = (abstractStaff.ymax - abstractStaff.ymin) * 0.25

                val measures = LinkedList<IStreamable>()
                for (abstractMeasure in abstractStaff.measures) {

                    val chords = LinkedList<Chord>()
                    for (glyph in abstractMeasure.glyphs) {
                        if (glyph is GlyphNote) {
                            // infer pitch
                            val pos = relativePos(glyph.y, abstractStaff.ymax, gap)
                            val noteName: String? = clefmap[pos]
                            val note: Note = if (noteName != null) {
                                Note(glyph, noteName)
                            } else {
                                Log.d(TAG, "Note out of bounds at $pos")
                                Note(glyph, "Unknown")
                            }
                            val chord = Chord(note)
                            chords.add(chord)
                        }
                    }
                    // TODO: Merge chords
                    // TODO: Consider accidentals
                    val part = Part(chords)
                    measures.add(part)
                }
                val staff = Stream(measures)
                staffs.add(staff)
            }
            return Stream(staffs)
        }

        private val relativePos = { notehead:    Double,
                            staffbottom: Double,
                            staffgap:    Double ->
            (2.0*(staffbottom - notehead)/staffgap).roundToInt()
        }

        fun flattenStream(stream: Stream) : Stream {
            val list: MutableList<Part> = mutableListOf()
            stream.first()
            stream.prevPartChord()
            while (stream.nextPartChord() != null) {
                val part = stream.currPart()
                if (part != null) {
                    list.add(part)
                }
            }
            return Stream(list)
        }

    }

}
package com.kocuni.jsontest

import java.util.*
import kotlin.collections.ArrayList

class Song {
    /**
     * Ideas:
     * Song
     *   System
     *     Staff
     *     Measure
     *       Glyphs
     *   toStream
     *
     * Stream
     *   Chord
     *     Note
     *       pitch
     *       length (unimportant for now)
     *   nextMeasure
     *   prevMeasure
     *   nextChord
     *   prevChord
     *
     * Tutor
     *   'should hold a stream'
     *   'should be user controllable'
     *   'communicate with JNI'
     *   'likely in C++, detect dominant frequencies and amplitude'
     *   'if within sufficient margins, give positive callback'
     *   'otherwise give neutral or negative callback'
     *
     *   Cpp
     *   jni_bridge
     *   AudioEngine
     *   Callback class?
     *   Utils
     *     FT/Spectrogram
     */
    var staffs: List<SystemStaff> = ArrayList()
    var isOneHanded: Boolean = true
    val systems: TreeMap<Int, Array<Int>> = TreeMap()
}

class SystemStaff {
    var ymax: Double = 0.0
    var ymin: Double = 0.0

    var measures: List<Measure> = ArrayList()
    var staffs: List<Staff> = ArrayList()
}

class Staff {
    var top: Double = 0.0
    var bottom: Double = 0.0
}

class Measure {
    var glyphs: List<Glyph> = ArrayList()
    var index: Int = -1
}

open class Glyph(val x: Double = 0.0,
                 val y: Double = 0.0,
                 val h: Double = 0.0,
                 val w: Double = 0.0) : Comparable<Glyph> {
    override fun compareTo(other: Glyph): Int {
        return this.x.compareTo(other.x)
    }
}

class GlyphNote(x: Double = 0.0,
                y: Double = 0.0,
                h: Double = 0.0,
                w: Double = 0.0) : Glyph(x, y, h, w) {
            val length: Double = 1.0
}

class GlyphAccidental(x: Double = 0.0,
                      y: Double = 0.0,
                      h: Double = 0.0,
                      w: Double = 0.0) : Glyph(x, y, h, w) {
    constructor(x: Double, y: Double, h: Double, w: Double, type: String) : this() {
        this.type = type
    }
    var type: String = "Natural"
}

class GlyphClef(x: Double = 0.0,
                y: Double = 0.0,
                h: Double = 0.0,
                w: Double = 0.0) : Glyph(x, y, h, w) {
    constructor(x: Double, y: Double, h: Double, w: Double, type: String) : this() {
        this.type = type
    }
    var type: String = "gClef"
}

class SongStream(song: Song) {
    val stream = TreeMap<Int, Chord>()
    /**
     * Traverse each measure in the song, generate chords and measures
     *
     * One handed case first
     */
    init {
        song.systems
    }

    override fun toString(): String {
        return super.toString()
    }
}

object SongStreamFactory {

    fun makeSongStream(song: Song): SongStream {

        for (staff in song.staffs) {
            for (measure in staff.measures) {
                for (glyph in measure.glyphs) {
                    if (glyph is GlyphNote) {
                        // infer pitch add
                        val chord = Chord()
                        val note = Note()
                        chord.notes.add(note)
                    }
                }
            }
        }

        return SongStream(song)
    }
}

/**
 * A list of notes that must be played at
 * the same time.
 */
class Chord() {
    val notes: ArrayList<Note> = ArrayList()
    var timeIndex: Double = 0.0;

    // merge the chords
    operator fun plus(o: Chord) : Chord {
        val chord = Chord()
        chord.notes.addAll(this.notes)
        chord.notes.addAll(o.notes)
        chord.timeIndex = (this.timeIndex + o.timeIndex)/2.0
        return chord
    }

}

class Note(){
    val pitch: Double = 0.0
    val name: String = "C4"
    val glyph: GlyphNote = GlyphNote()
}

enum class Clef {
    GCLEF, FCLEF, CCLEF
    //G4 ,   F3 ,   C4
    // Unfortunately, the meaning
    // of clefs is dependent on their
    // position which itself is difficult
    // to analyze
}

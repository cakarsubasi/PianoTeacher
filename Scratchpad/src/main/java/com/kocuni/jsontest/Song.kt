package com.kocuni.jsontest

import javax.lang.model.type.UnionType

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
    var systems: List<SystemStaff> = ArrayList<SystemStaff>()
}

class SystemStaff {
    var ymax: Double = 0.0
    var ymin: Double = 0.0

    var measures: List<Measure> = ArrayList<Measure>()
    var staffs: List<Staff> = ArrayList<Staff>();
}

class Staff {
    var top: Double = 0.0
    var bottom: Double = 0.0
}

class Measure {
    var glyphs: List<Glyph> = ArrayList<Glyph>()
}

open class Glyph(val x: Double = 0.0,
                 val y: Double = 0.0,
                 val h: Double = 0.0,
                 val w: Double = 0.0) : Comparable<Glyph> {
    override fun compareTo(other: Glyph): Int {
        return this.x.compareTo(other.x);
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

class SongStream() {

}

/**
 * A list of notes that must be played at
 * the same time.
 */
class Chord() {

    data class Pitch(val pitch: Double, val name: String = "C4") { }
    val notes: ArrayList<Pitch> = ArrayList<Pitch>()

}

class Note(){
    val pitch: Double = 0.0
    val name: String = "C4"
}

enum class Clef {
    GCLEF, FCLEF, CCLEF
    //G4 ,   F3 ,   C4
    // Unfortunately, the meaning
    // of clefs is dependent on their
    // position which itself is difficult
    // to analyze
}

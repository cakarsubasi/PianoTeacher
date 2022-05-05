package com.kocuni.pianoteacher.music

import java.util.*
import kotlin.collections.ArrayList

class AbstractSong {
    var staffs: List<SystemStaff> = ArrayList()
    var isOneHanded: Boolean = true
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


class Part() {

}

/**
 * A list of notes that must be played at
 * the same time.
 */
class Chord() {
    val notes: ArrayList<Note> = ArrayList()
    var timeIndex: Double = 0.0;

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

}

class Note(){
    constructor(glyph: GlyphNote, name: String) : this(name) {
        this.glyph = glyph
    }
    constructor(name: String) : this() {
        this.name = name
    }

    var pitch: Double = 0.0
    var name: String = "C4"
    var glyph: GlyphNote = GlyphNote()
}

enum class Clef {
    GCLEF, FCLEF, CCLEF
    //G4 ,   F3 ,   C4
    // Unfortunately, the meaning
    // of clefs is dependent on their
    // position which itself is difficult
    // to analyze
}

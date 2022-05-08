package com.kocuni.pianoteacher.music

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

class Staff() {
    constructor(top: Double, bottom: Double) : this() {
        this.top = top
        this.bottom = bottom
    }

    var top: Double = 0.0
    var bottom: Double = 0.0
}

class Measure() {
    constructor(glyphs: List<Glyph>) : this() {
        this.glyphs = glyphs
    }

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


enum class Clef {
    GCLEF, FCLEF, CCLEF
    //G4 ,   F3 ,   C4
    // Unfortunately, the meaning
    // of clefs is dependent on their
    // position which itself is difficult
    // to analyze
}

package com.kocuni.pianoteacher.ui.music

import androidx.compose.ui.graphics.Color
import com.kocuni.pianoteacher.music.Stream

class NoteBlock() : Block {
    constructor(note: Stream.Chord) : this() {
        name = note.notes[0].name
        this.color = Block.Colors.getColor(name)
    }
    constructor(name: String) : this() {
        this.name = name
        this.color = Block.Colors.getColor(name)
    }
    override var name: String = "Unknown"
    override var color: Color = Block.Colors.black
}
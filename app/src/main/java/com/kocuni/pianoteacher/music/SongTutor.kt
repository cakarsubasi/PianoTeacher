package com.kocuni.pianoteacher.music

import be.tarsos.dsp.util.PitchConverter
import kotlinx.coroutines.*

class SongTutor (var stream: Stream){

    init {
        // TODO: separate hands
        stream = Stream.Builder.flattenStream(stream)
    }

    enum class STATE {
        CORRECT,
        FALSE,
        IDLE,
    }

    val endToEnd = blockify(stream)

    val callback: (() -> Unit)? = null
    var autoAdvance: Boolean = false

    fun beginTutor(freq: Float) : STATE {
        val state: STATE
        if (freq == -1.0F) {
            state = STATE.IDLE
        } else if (matchNote(freq)) {
            state = STATE.CORRECT
            if (autoAdvance) {
                stream.nextChord()
            }
        } else {
            state = STATE.FALSE
        }
        return state
    }

    fun next() {
        stream.nextChord()
    }

    fun prev() {
        stream.prevChord()
    }

    fun nextMeasure() {
        stream.nextPartChord()
    }

    fun prevMeasure() {
        stream.prevPartChord()
    }

    /**
     * From the stream, get the latest frequency
     * Get the note for that frequency
     * compare to the current note
     */
    fun matchNote(detected: Float): Boolean {
        val curr = stream.currChord()
        return if (curr != null) {
            val expected = getMidi(curr)
            val played = PitchConverter.hertzToMidiKey(detected.toDouble()) - 12
            (expected == played)
        } else {
            false
        }
    }

    fun endSong() {

    }

    private fun blockify(streamOrPart: IStreamable) : List<NoteBlock> {
        var note = streamOrPart.first()
        val blocks: MutableList<NoteBlock> = mutableListOf()
        while (note != null) {
            blocks.add(NoteBlock(note))
            note = streamOrPart.nextChord()
        }
        streamOrPart.first()
        return blocks
    }

    /**
     * Only check the first note
     */
    private fun getMidi(chord: Stream.Chord): Int {
        return MidiTable.table[chord.notes[0].name] ?: -1
    }

    fun getNextNMeasures(n: Int) : Pair<Int, List<NoteBlock>> {
        val pos = stream.idx
        val point: Int = stream[pos].idx
        val list: MutableList<NoteBlock> = mutableListOf()

        for (i in pos until pos + n) {
            if (i == stream.size) {
                break
            }
            val part = stream[i] as Stream.Part
            part.chords.forEach {
                list.add(NoteBlock(it))
            }
        }
        return Pair(point, list)
    }

    fun getNextNBlocks(n: Int) : List<NoteBlock> {
        val list : MutableList<NoteBlock> = mutableListOf()
        var chord = stream.currChord()
        if (chord == null) {
            return list
        } else {
            list.add(NoteBlock(chord))
        }
        for (i in 0 until n-1) {
            chord = stream.nextChord()
            if (chord != null) {
                list.add(NoteBlock(chord))
            } else {
                break
            }
        }
        for (i in 0 until list.size-1) {
            stream.prevChord()
        }
        return list
    }

    interface Block {

    }

    class NoteBlock() : Block {
        constructor(note: Stream.Chord) : this() {
            name = note.notes[0].name
        }
        constructor(name: String) : this() {
            this.name = name
        }
        var name: String = "C0"
    }

    class MeasureBreak : Block {

    }


}
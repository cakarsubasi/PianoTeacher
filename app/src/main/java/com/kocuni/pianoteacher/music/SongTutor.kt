package com.kocuni.pianoteacher.music

import be.tarsos.dsp.util.PitchConverter
import kotlinx.coroutines.*
import java.util.Collections.copy

class SongTutor (val scope: CoroutineScope = GlobalScope, val stream: Stream){

    enum class STATE {
        CORRECT,
        FALSE,
        IDLE,
    }

    /**
     * Construct with a stream
     * Methods:
     * next() -> next chord
     * prev() -> prev chord
     * nextMeasure() -> next nonempty measure
     * prevMeasure() -> previous nonempty measure
     * nextStaff() -> first nonempty measure in the next staff
     * prevStaff() -> first nonempty measure in the previous staff
     * beginning() -> return to the beginning
     * endSong() -> to execute after the last chord is played
     */

    val endToEnd = flatten(stream)

    val callback: (() -> Unit)? = null
    var autoAdvance: Boolean = false

    lateinit var tutorJob: Job
    private val tutorScope = scope

    fun beginTutor(freq: Float) : STATE {
        var state = STATE.IDLE
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

    private fun flatten(streamOrPart: IStreamable) : List<NoteBlock> {
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

    class NoteBlock(val note: Stream.Chord) {

    }


}
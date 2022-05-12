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

    fun beginTutor() {
        tutorJob = tutorScope.launch(Dispatchers.Default) {

        }
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
    fun matchNote(): Boolean {

        return false
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

    class NoteBlock(val note: Stream.Chord) {

    }


}
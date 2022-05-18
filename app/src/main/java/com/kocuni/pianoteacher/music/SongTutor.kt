package com.kocuni.pianoteacher.music

import androidx.compose.ui.graphics.Color
import be.tarsos.dsp.util.PitchConverter
import com.kocuni.pianoteacher.music.data.MidiTable
import com.kocuni.pianoteacher.music.data.TutorableSong
import com.kocuni.pianoteacher.music.data.Voices

class SongTutor() {
    private lateinit var song: TutorableSong
    var stream: Stream = Stream(listOf())

    constructor(stream: Stream) : this() {
        this.song = TutorableSong(stream, listOf(Voices.SOPRANO))
        this.stream = song.SOPRANO()
    }

    constructor(song: TutorableSong) : this() {
        this.song = song
        stream = song.SOPRANO()
    }

    enum class STATE {
        CORRECT,
        FALSE,
        IDLE,
    }

    val endToEnd = blockify(stream)

    val callback: (() -> Unit)? = null
    var autoAdvance: Boolean = false

    /**
     * Called every frame
     *
     * @param freq: Frequency detected by the pitch detector, -1.0f if no frequency detected
     * @return STATE of the tutor
     *
     * TODO: less invariance
     */
    fun onUpdate(freq: Float) : STATE {
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

    fun getCurrentNote() : Stream.Chord? {
        return stream.currChord()
    }

    fun next() {
        val chord = stream.nextChord()
        if (chord == null)
            stream.last()
    }

    fun prev() {
        val chord = stream.prevChord()
        if (chord == null)
            stream.first()
    }

    fun nextMeasure() {
        val chord = stream.nextPartChord()
        if (chord == null)
            stream.last()
    }

    fun prevMeasure() {
        val chord = stream.prevPartChord()
        if (chord == null)
            stream.first()
    }

    fun beginning() {
        stream.first()
    }

    /**
     * From the stream, get the latest frequency
     * Get the note for that frequency
     * compare to the current note
     */
    private fun matchNote(detected: Float): Boolean {
        val curr = stream.currChord()
        return if (curr != null) {
            val expected = getMidi(curr)
            val played = PitchConverter.hertzToMidiKey(detected.toDouble())
            (expected == played)
        } else {
            false
        }
    }

    fun getNoteName(frequency: Float): String? {
        val midiKey =
            PitchConverter.hertzToMidiKey(frequency.toDouble())

        return MidiTable.midiToKey[midiKey]
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

    fun getNextNMeasures(n: Int) : Pair<Int, List<Block>> {
        val pos = stream.idx
        val point: Int = stream[pos].idx
        val list: MutableList<Block> = mutableListOf()

        for (i in pos until pos + n) {
            if (i == stream.size) {
                break
            }
            val part = stream[i] as Stream.Part
            part.chords.forEach {
                list.add(NoteBlock(it))
            }
            list.add(MeasureBlock())
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
        var name: String
        var color: Color

        object Colors {
            val measure = Color(0xFF888888)
            val C = Color(0xFF8080FF)
            val D = Color(0xFFBF80FF)
            val E = Color(0xFFFF80FF)
            val F = Color(0xFFFF80BF)
            val G = Color(0xFFFF8080)
            val A = Color(0xFFFFBF80)
            val B = Color(0xFFFFFF80)
            val black = Color(0xFF000000)

            fun getColor(name: String): Color {
                return when (name[0].lowercase()) {
                    "c" -> C
                    "d" -> D
                    "e" -> E
                    "f" -> F
                    "g" -> G
                    "a" -> A
                    "b" -> B
                    else -> black
                }
            }
        }
    }

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

    class MeasureBlock : Block {
        override var name: String = ""
        override var color: Color = Block.Colors.measure
    }


}
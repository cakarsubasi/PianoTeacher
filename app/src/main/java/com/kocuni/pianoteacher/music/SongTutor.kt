package com.kocuni.pianoteacher.music

import be.tarsos.dsp.util.PitchConverter
import com.kocuni.pianoteacher.music.data.MidiTable
import com.kocuni.pianoteacher.music.data.TutorableSong
import com.kocuni.pianoteacher.music.data.Voices
import com.kocuni.pianoteacher.ui.music.Block
import com.kocuni.pianoteacher.ui.music.MeasureBlock
import com.kocuni.pianoteacher.ui.music.NoteBlock

class SongTutor() {
    private lateinit var song: TutorableSong
    private val MIN_HOLD_COUNT = 3

    // game variables
    private var trueBuffer: Int = 0
    private var falseBuffer: Int = 0
    private var lastNote: String = "Unknown"

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

    var autoAdvance: Boolean = false

    fun getVoices() : List<String> {
        val voices: MutableList<String> = mutableListOf()
        for (voice in song.voices) {
            when (voice) {
                Voices.SOPRANO -> voices.add("SOPRANO")
                Voices.ALTO -> voices.add("ALTO")
                Voices.TENOR -> voices.add("TENOR")
                Voices.BASS -> voices.add("BASS")
            }
        }
        return voices
    }

    fun setVoice(voice: String) {
        when (voice) {
            "SOPRANO" -> stream = song.SOPRANO()
            "TENOR" -> stream = song.TENOR()
        }
    }

    /**
     * Called every frame
     *
     * @param freq: Frequency detected by the pitch detector, -1.0f if no frequency detected
     * @return Pair: Last played note, STATE of the tutor
     *
     */
    fun onUpdate(freq: Float) : Pair<String, STATE> {
        val state: STATE
        // clear the buffer if no detections
        if (freq == -1.0F) {
            trueBuffer = 0
            falseBuffer = 0
            state = STATE.IDLE
        } else if (matchNote(freq)) {
            ++trueBuffer
            if (trueBuffer >= MIN_HOLD_COUNT) {
                trueBuffer = 0
                falseBuffer = 0
                state = STATE.CORRECT
                lastNote = getNoteName(freq) ?: "Unknown"
                if (autoAdvance) {
                    stream.nextChord()
                }
            } else {
                state = STATE.IDLE
            }
        } else {
            ++falseBuffer
            trueBuffer = 0
            state = if (falseBuffer >= MIN_HOLD_COUNT) {
                lastNote = getNoteName(freq) ?: "Unknown"
                STATE.FALSE
            } else {
                STATE.IDLE
            }
        }
        return Pair(lastNote, state)
    }

    fun getCurrentNote() : Stream.Chord? {
        return stream.currChord()
    }

    fun next(): Boolean {
        val chord = stream.nextChord()
        if (chord == null) {
            stream.last()
            return false
        }
        return true
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
        val pos = stream.idx
        var point: Int = stream[pos].idx
        val list: MutableList<NoteBlock> = mutableListOf()

        for (i in pos until stream.size) {
            val part = stream[i] as Stream.Part

            part.chords.forEachIndexed { index, chord ->
                if (index >= point)
                    list.add(NoteBlock(chord))
            }
            if (list.size >= n) {
                break
            }
            point = 0
        }
        return try {
            list.subList(0, n)
        } catch (e: Exception) {
            list
        }
    }


}
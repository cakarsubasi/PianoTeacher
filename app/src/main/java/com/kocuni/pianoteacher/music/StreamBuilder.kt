package com.kocuni.pianoteacher.music

import android.util.Log
import com.kocuni.pianoteacher.music.data.ClefMaps
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.roundToInt

object StreamBuilder {
    private const val TAG = "StreamBuilder"

    /**
     * Use this to construct songs for the Song Tutor from inference results.
     */

    fun build(abstractSong: AbstractSong) : Stream {
        return if (abstractSong.isOneHanded) { // one handed
            val clef = "gClef"
            val stream = buildSingleStream(abstractSong.staffs, clef)
            stream
        } else { // two handed
            val clef1 = "gClef"
            val clef2 = "fClef"
            // split staffs
            val staffsR = abstractSong.staffs.filterIndexed { index, _ ->
                index % 2 == 0
            }
            val staffsL = abstractSong.staffs.filterIndexed { index, _ ->
                index % 2 == 1
            }
            val streamR = buildSingleStream(staffsR, clef1)
            val streamL = buildSingleStream(staffsL, clef2)
            Stream(listOf(streamR, streamL)) // stream is not private and easily accessible!
        }
    }

    private fun buildSingleStream(abstractStaffs: List<SystemStaff>, clef: String) : Stream {
        // determine which mapping will be used
        val clefmap: HashMap<Int, String> = ClefMaps.clefs(clef) ?: ClefMaps.gclefmap

        val staffs = LinkedList<IStreamable>()
        for (abstractStaff in abstractStaffs) {
            val bottom = abstractStaff.staffs[0].bottom
            val top = abstractStaff.staffs[0].top
            val gap = (bottom - top) * 0.25

            val measures = LinkedList<IStreamable>()
            for (abstractMeasure in abstractStaff.measures) {

                val chords = LinkedList<Stream.Chord>()
                for (glyph in abstractMeasure.glyphs) {
                    if (glyph is GlyphNote) {
                        // infer pitch
                        val pos = relativePos(glyph.y, bottom, gap)
                        val noteName: String? = clefmap[pos]
                        val note: Stream.Note
                        if (noteName != null) {
                            // this is a dirty hack to avoid creating invalid notes
                            note = Stream.Note(glyph, noteName)
                            val chord = Stream.Chord(note)
                            chords.add(chord)
                        } else {
                            Log.d(TAG, "Note out of bounds at $pos")
                            //Note(glyph, "Unknown")
                        }
                    }
                }
                // TODO: Merge chords
                // TODO: Consider accidentals
                val part = Stream.Part(chords)
                measures.add(part)
            }
            val staff = Stream(measures)
            staffs.add(staff)
        }
        return Stream(staffs)
    }

    private val relativePos = { noteHead:    Double,
                        staffBottom: Double,
                        staffGap:    Double ->
        (2.0*(staffBottom - noteHead)/staffGap).roundToInt()
    }

    fun flattenStream(stream: Stream) : Stream {
        val list: MutableList<Stream.Part> = mutableListOf()
        stream.first()
        stream.prevPartChord()
        while (stream.nextPartChord() != null) {
            val part = stream.currPart()
            if (part != null) {
                list.add(part)
            }
        }
        return Stream(list)
    }

}
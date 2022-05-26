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
            Stream(listOf(stream))
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

            val soprano = buildSingleStream(staffsR, clef1)
            // Dirty hack
            val alto = buildSingleStream(staffsL, clef1)
            val tenor = buildSingleStream(staffsL, clef2)
            Stream(listOf(soprano, tenor, alto)) // stream is not private and easily accessible!
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
                // for accidentals
                var offset = 0
                val chords = LinkedList<Stream.Chord>()
                for (glyph in abstractMeasure.glyphs) {
                    if (glyph is GlyphNote) {
                        // infer pitch
                        val pos = relativePos(glyph.y, bottom, gap)
                        var noteName: String = clefmap[pos] ?: "Unknown"
                        // reset offset
                        /*
                        Accurate application would require keeping track of the note in each line too
                         */
                        noteName = applyAccidental(noteName, offset)
                        offset = 0

                        Log.d(TAG,noteName)
                        val note: Stream.Note
                        if (noteName != "Unknown") {
                            // this is a dirty hack to avoid creating invalid notes
                            note = Stream.Note(glyph, noteName)
                            val chord = Stream.Chord(note)
                            chords.add(chord)
                        } else {
                            Log.d(TAG, "Note out of bounds at $pos")
                            //Note(glyph, "Unknown")
                        }
                    } else if (glyph is GlyphAccidental) {
                        offset = when (glyph.type.lowercase()) {
                            "accidentalflat" -> -1
                            "accidentalsharp" -> 1
                            "flat" -> -1
                            "sharp" -> 1
                            else -> 0
                        }
                        Log.d(TAG, "Accidental ${glyph.type}, $offset")
                    }
                }
                // TODO: Merge chords
                val part = Stream.Part(chords)
                measures.add(part)
            }
            val staff = Stream(measures)
            staffs.add(staff)
        }
        return Stream(staffs)
    }

    private fun applyAccidental(noteName: String, offset: Int) : String {
        if (noteName == "Unknown")
            return noteName
        try {
            var note: Char = noteName[0]
            var octave: Int = noteName[1].digitToInt()
            val sharp: Boolean = when(offset) {
                1 -> when (note) {
                    'E' -> false
                    'B' -> false
                    else -> true
                }
                -1 -> when(note) {
                    'C' -> false
                    'F' -> false
                    else -> true
                }
                else -> false
            }
            octave = when (offset) {
                1 -> when (note) {
                    'B' -> octave + 1
                    else -> octave
                }
                -1 -> when (note) {
                    'C' -> octave - 1
                    else -> octave
                }
                else -> octave
            }
            // don't swap these LUTs around
            note = when (offset) {
                1 -> when (note) {
                    'E' -> 'F'
                    'B' -> 'C'
                    else -> note
                }
                -1 -> when (note) {
                    'B' -> 'A'
                    'E' -> 'D'
                    'A' -> 'G'
                    'D' -> 'C'
                    'G' -> 'F'
                    else -> note
                }
                else -> note
            }

            return if (sharp) "$note$octave#" else "$note$octave"
        } catch (e: Exception) {
            return noteName
        }
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
package com.kocuni.pianoteacher.music

/**
 * Hardcoded sample songs
 * TODO(implement)
 */
object SampleSongs {

    fun song1() : Stream {
        val chordify = {str: String -> Stream.Chord(Stream.Note(str))}
        val generatePart = { str: String ->
            val list: MutableList<Stream.Chord> = mutableListOf()
            str.splitToSequence(" ").forEach {
                list.add(chordify(it))
            }
            Stream.Part(list)
        }

        val measure1 = "C4 D4 E4 D4"
        val measure2 = "A4 C4 D4 A4"
        val measure3 = "B4 C5 D4 G4"
        val measure4 = "C4 D4 E4 D4"

        return Stream(listOf(
            generatePart(measure1),
            generatePart(measure2),
            generatePart(measure3),
            generatePart(measure4)
        ))
    }
}
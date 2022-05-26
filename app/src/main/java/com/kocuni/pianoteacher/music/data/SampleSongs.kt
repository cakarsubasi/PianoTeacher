package com.kocuni.pianoteacher.music.data

import com.kocuni.pianoteacher.music.Stream
import com.kocuni.pianoteacher.music.StreamBuilder
import com.kocuni.pianoteacher.utils.JSONParser
import org.json.JSONObject
import java.io.File
import java.io.FileReader

/**
 * Hardcoded sample songs
 * TODO(implement)
 */
object SampleSongs {
    val chordify = {str: String -> Stream.Chord(Stream.Note(str)) }
    val generatePart = { str: String ->
        val list: MutableList<Stream.Chord> = mutableListOf()
        str.splitToSequence(" ").forEach {
            list.add(chordify(it))
        }
        Stream.Part(list)
    }

    fun song1() : Stream {

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

    fun song2(): Stream {
        val measure1 = generatePart("C4 D4 E4 D4")
        val measure2 = generatePart("A4 C4 D4 A4")
        val measure3 = generatePart("B4 C5 D4 G4")
        val measure4 = generatePart("C4 D4 E4 D4")

        val stream1 = Stream(listOf(measure1, measure2, measure3))
        val stream2 = Stream(listOf(measure4, measure2, measure3))

        return StreamBuilder.flattenStream(Stream(listOf(stream1, stream2, stream1)))
    }

    fun songFromJson() {


        val buffer = CharArray(100_000)
        val examplesong = File("Scratchpad/src/main/java/com/kocuni/jsontest/song.json")
        val fileReader = FileReader(examplesong)
        fileReader.read(buffer)

        val jsonstr = String(buffer)

        val songjson = JSONObject(jsonstr)
        println(songjson.toString(4))

        //val abstractsong = JSONParser.parse(songjson)
    }
}
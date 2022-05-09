package com.kocuni.jsontest

import org.json.JSONObject
import java.io.File
import java.io.FileReader

fun main() {
    println("Hello world")
    // this buffer size might need to get a lot larger
    val buffer = CharArray(100_000)
    val examplesong = File("Scratchpad/src/main/java/com/kocuni/jsontest/song.json")
    val fileReader = FileReader(examplesong)
    fileReader.read(buffer)

    val jsonstr: String = String(buffer)

    val songjson = JSONObject(jsonstr)
    println(songjson.toString(4))

    val abstractsong = JSONParser.parse(songjson)

}

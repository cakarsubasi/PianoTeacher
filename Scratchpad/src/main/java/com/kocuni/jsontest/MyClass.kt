package com.kocuni.jsontest

import org.json.JSONObject
import java.io.File
import java.io.FileReader
import java.io.Reader

class MyClass {
    var x: Int = 0
}

fun main() {
    println("Hello world")
    // this buffer size might need to get a lot larger
    val buffer = CharArray(100_000)
    val examplesong = File("C:\\Repositories\\PianoTeacher\\Scratchpad\\src\\main\\java\\com\\kocuni\\jsontest\\song.json")
    val fileReader = FileReader(examplesong)
    fileReader.read(buffer)

    val jsonstr: String = String(buffer)
    println(jsonstr)

    val abstractSong: JSONObject = JSONObject(jsonstr)

    println(abstractSong["type"])

}
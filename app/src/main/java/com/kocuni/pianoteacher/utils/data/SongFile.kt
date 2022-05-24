package com.kocuni.pianoteacher.utils.data

import com.kocuni.pianoteacher.music.TutorableSong
import com.kocuni.pianoteacher.utils.FileManager.Companion.getSongFromJSONStream
import java.io.File
import java.nio.file.Path

class SongFile(var name: String, var path: Path) {


    fun getTutorable(): TutorableSong {

        val file: File = path.toFile()

        return getSongFromJSONStream(file.inputStream())
    }

    override fun toString(): String {
        return "Song: $name in $path"
    }
}
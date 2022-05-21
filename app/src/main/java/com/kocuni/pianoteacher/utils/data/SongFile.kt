package com.kocuni.pianoteacher.utils.data

import com.kocuni.pianoteacher.music.data.TutorableSong
import com.kocuni.pianoteacher.utils.FileManager.Companion.getSongFromJSONStream
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

class SongFile(var name: String, var path: Path) {


    fun getTutorable(): TutorableSong {

        val file: File = path.toFile()

        return getSongFromJSONStream(file.inputStream())
    }

    override fun toString(): String {
        return "Song: $name in $path"
    }
}
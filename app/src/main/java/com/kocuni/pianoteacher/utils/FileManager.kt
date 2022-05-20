package com.kocuni.pianoteacher.utils

import android.os.Environment
import com.kocuni.pianoteacher.music.data.TutorableSong
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.lang.NullPointerException


class FileManager {


    companion object {
        fun saveJSON() {



        }

        fun loadJSON() {

        }

        @Throws(NullPointerException::class)
        fun getSongFromJSONStream(f: InputStream) : TutorableSong {
            val jsonStr = f.bufferedReader().readLine()
            if (jsonStr != null) {
                val abstractSong = JSONParser.parse(JSONObject(jsonStr))
                return TutorableSong.buildTutorable(abstractSong = abstractSong)
            } else {
                throw NullPointerException("Could not read JSON String")
            }
        }
    }
}
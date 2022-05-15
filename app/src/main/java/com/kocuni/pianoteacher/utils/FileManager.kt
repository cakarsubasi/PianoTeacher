package com.kocuni.pianoteacher.utils

import com.kocuni.pianoteacher.music.data.TutorableSong
import org.json.JSONObject
import java.io.InputStream
import java.lang.NullPointerException


class FileManager {


    companion object {
        fun saveJSON() {

        }

        fun loadJSON() {

        }

        /**
         * TODO: There might be an issue with how the songs are parsed
         * Since I get a lot of F4s on the only test performed.
         */
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
package com.kocuni.pianoteacher.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.kocuni.pianoteacher.R
import com.kocuni.pianoteacher.music.data.TutorableSong
import com.kocuni.pianoteacher.utils.data.SongFile
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.lang.NullPointerException
import java.nio.file.Files
import kotlin.io.path.name


class FileManager() {

    constructor(context: Context?) : this() {
        if (context != null)
            this.context = context
    }


    val TAG = "FileManager"
    val songs: MutableList<SongFile> = mutableListOf()
    lateinit var context: Context
    private lateinit var filesDir: File
    private var initialized: Boolean = false


    fun initialize() {
        if (!initialized) {
            filesDir = context.filesDir
            copyRawFiles()
            readAllFiles()
            Log.d(TAG, this.toString())
        }
    }

    fun readAllFiles () {
        val st = Files.walk(filesDir.toPath())
        st.filter {
            it.name.contains(Regex.fromLiteral(".json"))
        }.forEach {
            if (it != null) {
                songs.add(SongFile(it.name, it))
            }
        }
    }

    fun CreateFile() {

    }

    fun DeleteFile() {

    }

    private fun copyRawFiles() {
        val name1 = "muscima_45"
        val name2 = "muscima_46"
        val m1 = context.resources.openRawResource(R.raw.muscima_45)
        val m2 = context.resources.openRawResource(R.raw.muscima_46)

        val copy1 = File(filesDir, "$name1.json")
        val copy2 = File(filesDir, "$name2.json")

        if (!copy1.exists()) {
            copy1.createNewFile()
            val str1 = m1.bufferedReader().readLine()
            copy1.writeText(str1)
        }
        if (!copy2.exists()) {
            copy2.createNewFile()
            val str2 = m2.bufferedReader().readLine()
            copy2.writeText(str2)
        }
    }

    override fun toString(): String {
        var str = ""
        songs.forEach {
            str = str + it + "\n"
        }
        return str
    }


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
package com.kocuni.pianoteacher.utils

import android.content.Context
import android.util.Log
import com.kocuni.pianoteacher.R
import com.kocuni.pianoteacher.music.TutorableSong
import com.kocuni.pianoteacher.utils.data.SongFile
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.lang.NullPointerException
import java.nio.file.Files
import java.util.*
import kotlin.io.path.name


class FileManager() {


    constructor(context: Context?) : this() {
        if (context != null)
            this.context = context
    }

    val TAG = "FileManager"
    val songs: SortedSet<SongFile> = sortedSetOf()
    lateinit var context: Context
    private lateinit var filesDir: File
    private var initialized: Boolean = false


    fun initialize() {
        if (!initialized) {
            filesDir = context.filesDir
            copyRawFiles()
            readAllFiles()
            Log.d(TAG, this.toString())
            initialized = true
        }
    }

    fun readAllFiles() {
        songs.clear()
        val st = Files.walk(filesDir.toPath())
        st.filter {
            it.name.contains(Regex.fromLiteral(".json"))
        }.forEach {
            if (it != null) {
                songs.add(SongFile(it.name.removeSuffix(".json"), it))
            }
        }
    }



    fun createFile(filename: String, contents: String) {
        val file = File(filesDir, filename)
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(contents)
    }

    fun deleteFile(song: SongFile) : Boolean {
        if (songs.remove(song)) {
            val file = song.path.toFile()
            return (file.delete())
        }
        return false
    }

    /**
     * Test method to copy some built in raw files
     */
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

        private const val TAG = "FileManager"

        @Throws(NullPointerException::class)
        fun getSongFromJSONStream(f: InputStream) : TutorableSong {
            val jsonStr = f.bufferedReader().readLine()
            if (jsonStr != null) {
                try {
                    val abstractSong = JSONParser.parse(JSONObject(jsonStr))
                    return TutorableSong.buildTutorable(abstractSong = abstractSong)
                } catch (e: Exception) {
                    Log.e(TAG, "failed to parse song.")
                    throw NullPointerException("Could not read JSON String")
                }
            } else {
                throw NullPointerException("Could not read JSON String")
            }
        }
    }
}
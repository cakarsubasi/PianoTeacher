package com.kocuni.jsontest

import org.json.JSONObject
import java.io.File
import java.io.FileReader
import java.lang.String.format

fun main() {
    println("Hello world")
    // this buffer size might need to get a lot larger
    val buffer = CharArray(100_000)
    val examplesong = File("C:\\Repositories\\PianoTeacher\\Scratchpad\\src\\main\\java\\com\\kocuni\\jsontest\\song.json")
    val fileReader = FileReader(examplesong)
    fileReader.read(buffer)

    val jsonstr: String = String(buffer)
    println(jsonstr)

    val abstractSong = JSONObject(jsonstr)

    println(abstractSong.toString(4))

    val songObjects = abstractSong.getJSONObject("objects")

    //println(songObjects.getJSONObject("0").getJSONObject("objects").getJSONObject("measures"))
    val song: Song = Song()
    val systems = ArrayList<SystemStaff>()
    for (systemIndex in songObjects.keys()) {
        val jSystem: JSONObject = songObjects.getJSONObject(systemIndex).getJSONObject("objects")
        for (index in jSystem.keys()) {
            // measures, boundaries, staffs
            //println(format("%s : %s", index, jSystem[index]));
            val ymin: Double = jSystem.getJSONObject("boundaries").getDouble("ymin")
            val ymax: Double = jSystem.getJSONObject("boundaries").getDouble("ymax")
            val jStaffs = jSystem.getJSONObject("staffs")
            val jMeasures = jSystem.getJSONObject("measures")

            val staffs = ArrayList<Staff>()
            for (staffIndex in jStaffs.keys()) {
                val top: Double = jStaffs.getJSONObject(staffIndex).getJSONObject("objects").getDouble("top")
                val bottom: Double = jStaffs.getJSONObject(staffIndex).getJSONObject("objects").getDouble("bottom")
                // construct staffs
                val staff = Staff()
                staff.top = top
                staff.bottom = bottom
                staffs.add(staff)
            }
            val measures = ArrayList<Measure>()
            for (measureIndex in jMeasures.keys()) {
                // each measure
                val jMeasureObjs = jMeasures.getJSONObject(measureIndex).getJSONObject("objects")

                val glyphs = ArrayList<Glyph>()
                for (noteIndex in jMeasureObjs.keys()) {
                    val jNoteObject = jMeasureObjs.getJSONObject(noteIndex)
                    val glyphType = jNoteObject.getString("type")
                    val x = jNoteObject.getJSONObject("objects").getDouble("x")
                    val y = jNoteObject.getJSONObject("objects").getDouble("y")
                    val h = jNoteObject.getJSONObject("objects").getDouble("h")
                    val w = jNoteObject.getJSONObject("objects").getDouble("w")

                    // TODO: typed glyphs
                    val glyph = Glyph(x, y, h, w)
                    glyphs.add(glyph)
                }
                // construct measures
                val measure = Measure()
                measure.glyphs = glyphs
            }

            // construct system
            val system = SystemStaff()
            system.measures = measures
            system.staffs = staffs
            system.ymax = ymax
            system.ymin = ymin
            systems.add(system)
        }
        song.systems = systems
    }

}

fun constructSong(songjson: JSONObject) {

}
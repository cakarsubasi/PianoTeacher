package com.kocuni.pianoteacher.utils

import com.kocuni.pianoteacher.music.*
import org.json.JSONObject

object JSONParser {

        fun parse(songjson: JSONObject): AbstractSong {
            val songObjects = songjson.getJSONObject("objects")
            val isOneHanded = songjson.getBoolean("one_handed")

            val abstractSong = AbstractSong()
            val systems = ArrayList<SystemStaff>()

            for (systemIndex in songObjects.keys()) {
                val system = SystemStaff()
                val jSystem: JSONObject = songObjects.getJSONObject(systemIndex).getJSONObject("objects")

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
                    val jMeasureObjs =
                        jMeasures.getJSONObject(measureIndex).getJSONObject("objects")
                    val measure = Measure()
                    val glyphs = ArrayList<Glyph>()
                    for (noteIndex in jMeasureObjs.keys()) {
                        val jNoteObject = jMeasureObjs.getJSONObject(noteIndex)
                        val glyphType = jNoteObject.getString("type")
                        val x = jNoteObject.getJSONObject("objects").getDouble("x")
                        val y = jNoteObject.getJSONObject("objects").getDouble("y")
                        val h = jNoteObject.getJSONObject("objects").getDouble("h")
                        val w = jNoteObject.getJSONObject("objects").getDouble("w")

                        val glyph: Glyph = when (glyphType) {
                            "Note" -> GlyphNote(x, y, h, w)
                            "Accidental" -> {
                                val acc = jNoteObject.getJSONObject("objects").getString("accidentalType")
                                GlyphAccidental(x, y, h, w, acc)
                            }
                            "Clef" -> {
                                val acc = jNoteObject.getJSONObject("objects").getString("clefType")
                                GlyphClef(x, y, h, w, acc)
                            }
                            else -> Glyph(x, y, h, w)
                        }

                        glyphs.add(glyph)
                    }
                    // construct measures
                    measure.glyphs = glyphs
                    measure.index = Integer.parseInt(measureIndex)
                    measures.add(measure)
                }
                // construct system
                system.measures = measures
                system.staffs = staffs
                system.ymax = ymax
                system.ymin = ymin
                systems.add(system)
            }
            abstractSong.staffs = systems
            abstractSong.isOneHanded = isOneHanded
            return abstractSong
        }


}
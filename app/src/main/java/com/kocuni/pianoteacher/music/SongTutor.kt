package com.kocuni.pianoteacher.music

class SongTutor (val stream: Stream){

    /**
     * Construct with a stream
     * Methods:
     * next() -> next chord
     * prev() -> prev chord
     * nextMeasure() -> next nonempty measure
     * prevMeasure() -> previous nonempty measure
     * nextStaff() -> first nonempty measure in the next staff
     * prevStaff() -> first nonempty measure in the previous staff
     * beginning() -> return to the beginning
     * endSong() -> to execute after the last chord is played
     */

    val endToEnd = flatten(stream)

    /**
     *
     *
     */

    /**
     * From the stream, get the latest frequency
     * Get the note for that frequency
     * compare to the current note
     */
    fun matchNote(): Boolean {

        return false
    }



    private fun flatten(streamOrPart: IStreamable) : List<NoteBlock> {
        var note = streamOrPart.first()
        val blocks: MutableList<NoteBlock> = mutableListOf()
        while (note != null) {
            blocks.add(NoteBlock(note))
            note = streamOrPart.nextChord()
        }
        streamOrPart.first()
        return blocks
    }

    class NoteBlock(val note: Stream.Chord) {

    }


}
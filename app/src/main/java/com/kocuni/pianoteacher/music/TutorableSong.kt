package com.kocuni.pianoteacher.music

/**
 * I'm really running out of clever names here
 *
 * This
 */
class TutorableSong(rawStream: Stream, var voices: List<Voices> = listOf(Voices.SOPRANO)) {

    val streams = mutableListOf<Stream>()

    val SOPRANO: () -> Stream = {
        streams[0]
    }

    val TENOR: () -> Stream = {
        streams[1]
    }

    // TODO construct more intelligently
    init {
        if (voices.size == 1) {
            val st = StreamBuilder.flattenStream(rawStream)
            streams.add(st)
        } else {
            for (i in voices.indices) {
                val st = StreamBuilder.flattenStream(rawStream[i] as Stream)
                streams.add(st)
            }
        }
    }

    companion object {
        /**
         * Use this to construct songs for the Song Tutor from inference results.
         */
        fun buildTutorable(abstractSong: AbstractSong) : TutorableSong {
            val voices = mutableListOf<Voices>(Voices.SOPRANO)
            if (!abstractSong.isOneHanded) {
                voices.add(Voices.TENOR)
            }
            val rawStream = StreamBuilder.build(abstractSong = abstractSong)
            return TutorableSong(rawStream, voices)
        }
    }
}

/**
 * This is redundant as our system only supports 2 voices in the backend
 * Need to update a lot of code to cover four voice systems
 */
enum class Voices {
    SOPRANO,
    ALTO,
    TENOR,
    BASS,
}
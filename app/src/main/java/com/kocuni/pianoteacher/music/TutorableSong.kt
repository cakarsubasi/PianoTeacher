package com.kocuni.pianoteacher.music

/**
 * I'm really running out of clever names here
 *
 * This
 */
class TutorableSong() {

    val streams = mutableListOf<Stream>()
    private var streams2 = Streams()

    private data class Streams(
        val soprano: Stream? = null,
        val alto: Stream? = null,
        val tenor: Stream? = null,
        val bass: Stream? = null,
    ) {

    }

    var voices: List<Voices> = listOf()

    val SOPRANO: () -> Stream = {
        streams2.soprano ?: Stream(mutableListOf())
    }

    val ALTO: () -> Stream = {
        streams2.alto ?: Stream(mutableListOf())
    }

    val TENOR: () -> Stream = {
        streams2.tenor ?: Stream(mutableListOf())
    }

    val BASS: () -> Stream = {
        streams2.bass ?: Stream(mutableListOf())
    }

    constructor(
        soprano: Stream? = null,
        alto: Stream? = null,
        tenor: Stream? = null,
        bass: Stream? = null,
    ) : this() {
        val voices = mutableListOf<Voices>()
        if (soprano != null) {
            voices.add(Voices.SOPRANO)
        }
        if (alto != null) {
            voices.add(Voices.ALTO)
        }
        if (tenor != null) {
            voices.add(Voices.TENOR)
        }
        if (bass != null) {
            voices.add(Voices.BASS)
        }

        streams2 = Streams(soprano, alto, tenor, bass)
        this.voices = voices
    }

    companion object {
        /**
         * Use this to construct songs for the Song Tutor from inference results.
         */
        fun buildTutorable(abstractSong: AbstractSong) : TutorableSong {
            val rawStream = StreamBuilder.build(abstractSong = abstractSong)
            if (abstractSong.isOneHanded) {
                return TutorableSong(soprano = StreamBuilder.flattenStream(rawStream[0] as Stream))
            } else {
                return TutorableSong(
                    soprano = StreamBuilder.flattenStream(rawStream[0] as Stream),
                    alto =    StreamBuilder.flattenStream(rawStream[1] as Stream),
                    tenor =   StreamBuilder.flattenStream(rawStream[2] as Stream)
                )
            }
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
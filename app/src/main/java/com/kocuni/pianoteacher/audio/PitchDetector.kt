package com.kocuni.pianoteacher.audio

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.pitch.*

class PitchDetectorTest {

    val detector = FastYin(44100F, 2048)
}

class Detectors {
    lateinit var detectorFFT : FFTPitch
    lateinit var detectorAMDF : AMDF
    lateinit var detectorDW : DynamicWavelet
    lateinit var detectorMcLeod : McLeodPitchMethod

}
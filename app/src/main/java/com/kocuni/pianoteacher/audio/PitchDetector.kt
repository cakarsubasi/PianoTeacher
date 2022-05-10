package com.kocuni.pianoteacher.audio

import be.tarsos.dsp.pitch.FastYin
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchDetector

class PitchDetectorTest {

    val detector = FastYin(44100F, 2048)
}
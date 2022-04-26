//
// Created by explo on 2022-04-26.
//

#include "AudioEngine.h"
#include <oboe/oboe.h>
#include <android/log.h>



bool AudioEngine::start() {
    // TODO
    oboe::AudioStreamBuilder inBuilder;
    inBuilder.setDirection(oboe::Direction::Input)
    ->setChannelCount(mChannelCount)
    ->setSharingMode(oboe::SharingMode::Exclusive)
    ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
    ->setSampleRate(kSampleRate)
    ->setDataCallback(this);

    oboe::Result result = inBuilder.openStream(mRecordingStream);
    if (result != oboe::Result::OK) {
        __android_log_print(ANDROID_LOG_ERROR,
                            "AudioEngine",
                            "Error opening stream %s",
                            convertToText(result));
        return false;
    }
    mRecordingStream->requestStart();

    oboe::AudioStreamBuilder outBuilder;
    outBuilder.setDirection(oboe::Direction::Output)
    ->setChannelCount(mChannelCount)
    ->setSharingMode(oboe::SharingMode::Exclusive)
    ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
    ->setSampleRate(mRecordingStream->getSampleRate())
    ->setDataCallback(this);


    return true;
}

void AudioEngine::stop() {
    if (mRecordingStream) {
        oboe::Result result = mRecordingStream->stop();
        if (result != oboe::Result::OK) {
            __android_log_print(ANDROID_LOG_WARN,
                                "AudioEngine",
                                "Error stopping stream: %s",
                                convertToText(result));
        }
        result = mRecordingStream->close();
        if (result != oboe::Result::OK) {
            __android_log_print(ANDROID_LOG_ERROR,
                                "AudioEngine",
                                "Error closing stream: %s",
                                convertToText(result));
        } else {
            __android_log_print(ANDROID_LOG_WARN,
                                "AudioEngine",
                                "Successfully closed stream: %s",
                                convertToText(result));
        }
        mRecordingStream.reset();
    }

}

void AudioEngine::restart() {
    // TODO

}

oboe::DataCallbackResult
AudioEngine::onAudioReady(oboe::AudioStream *oboeStream,
                          void *audioData,
                          int32_t numFrames) {
    // sample code for sine wave generation
    float *floatData = (float *) audioData;
    for (int i = 0; i < numFrames; ++i) {
        float sampleValue = kAmplitude * sinf(mPhase);
        for (int j = 0; j < mChannelCount; j++) {
            floatData[i * mChannelCount + j] = sampleValue;
        }
        mPhase += mPhaseIncrement;
        if (mPhase >= kTwoPi) mPhase -= kTwoPi;
    }


    return oboe::DataCallbackResult::Continue;
}

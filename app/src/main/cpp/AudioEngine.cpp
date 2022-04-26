//
// Created by explo on 2022-04-26.
//

#include "AudioEngine.h"
#include <oboe/oboe.h>

void dataCallback() {
    // TODO
}

void errorCallback() {
    // TODO
}

bool AudioEngine::start() {
    // TODO
    oboe::AudioStreamBuilder inBuilder;
    inBuilder.setDirection(oboe::Direction::Input);
    inBuilder.setChannelCount(mInputChannelCount);
    inBuilder.setSharingMode(oboe::SharingMode::Exclusive);
    inBuilder.setPerformanceMode(oboe::PerformanceMode::LowLatency);

    oboe::Result result = inBuilder.openStream(mRecordingStream);
    if (result != oboe::Result::OK) {
        mSampleRate = oboe::kUnspecified;
        return false;
    }

}

void AudioEngine::stop() {
    // TODO
}

void AudioEngine::restart() {
    // TODO

}
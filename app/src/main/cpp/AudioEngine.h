//
// Created by explo on 2022-04-26.
//

#ifndef PIANO_TEACHER_AUDIOENGINE_H
#define PIANO_TEACHER_AUDIOENGINE_H

#include <oboe/oboe.h>
#include <cstdint>

class AudioEngine {
public:
    bool start();
    void stop();
    void restart();
private:
    int32_t mRecordingDeviceId = oboe::kUnspecified;
    const oboe::AudioFormat mFormat = oboe::AudioFormat::Float;
    int32_t mSampleRate = oboe::kUnspecified;
    const int32_t mInputChannelCount = oboe::ChannelCount::Mono;

    std::shared_ptr<oboe::AudioStream> mRecordingStream;

};

#endif //PIANO_TEACHER_AUDIOENGINE_H

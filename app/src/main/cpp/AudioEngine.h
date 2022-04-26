//
// Created by explo on 2022-04-26.
//

#ifndef PIANO_TEACHER_AUDIOENGINE_H
#define PIANO_TEACHER_AUDIOENGINE_H

#include <oboe/oboe.h>
#include <cstdint>
#include <math.h>

class AudioEngine: public oboe::AudioStreamDataCallback {
public:

    virtual ~AudioEngine() = default;

    bool start();
    void stop();
    void restart();

    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream,
                                          void *audioData,
                                          int32_t numFrames) override;
private:
    int32_t mRecordingDeviceId = oboe::kUnspecified;
    const oboe::AudioFormat mFormat = oboe::AudioFormat::Float;
    int32_t mSampleRate = oboe::kUnspecified;
    const int32_t mChannelCount = oboe::ChannelCount::Mono;

    std::mutex mLock;
    std::shared_ptr<oboe::AudioStream> mRecordingStream;
    std::shared_ptr<oboe::AudioStream> mPlaybackStream;

    static int constexpr    kSampleRate = 48000;
    static float constexpr  kAmplitude = 0.5f;
    static float constexpr  kFrequency = 440;
    static float constexpr  kPI = M_PI;
    static float constexpr  kTwoPi = kPI * 2;
    static double constexpr mPhaseIncrement = kFrequency * kTwoPi / (double) kSampleRate;
    // Keeps track of where the wave is
    float mPhase = 0.0;

};

#endif //PIANO_TEACHER_AUDIOENGINE_H

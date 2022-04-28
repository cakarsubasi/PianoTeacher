//
// Created by explo on 2022-04-26.
//

#ifndef PIANO_TEACHER_AUDIOENGINE_H
#define PIANO_TEACHER_AUDIOENGINE_H

#include <oboe/oboe.h>
#include <cstdint>
#include <math.h>
#include <android/log.h>
#include "minfft/minfft.h"
//#include "Callbacks.h"

class AudioEngine {
public:
    AudioEngine();
    virtual ~AudioEngine() = default;

    bool start();
    void stop();
    void restart();

private:
    static void sineWave(float* floatData, int32_t numFrames, float mPhase) {
        for (int i = 0; i < numFrames; ++i) {
            float sampleValue = kAmplitude * sinf(mPhase);
            for (int j = 0; j < mChannelCount; j++) {
                floatData[i * mChannelCount + j] = sampleValue;
            }
            mPhase += mPhaseIncrement;
            if (mPhase >= kTwoPi) mPhase -= kTwoPi;
        }
    }

    class InputCallback: public oboe::AudioStreamDataCallback {
    public:
        oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream,
                                              void *audioData,
                                              int32_t numFrames) override {

            return oboe::DataCallbackResult::Continue;
        }

    };

    class OutputCallback: public oboe::AudioStreamDataCallback {
    public:
        oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream,
                                              void *audioData,
                                              int32_t numFrames) override {

            float *floatData = (float *) audioData;

            sineWave(floatData, numFrames, mPhase);

            return oboe::DataCallbackResult::Continue;
        }
    private:
        float mPhase = 0.0;
        oboe::AudioStream *mStream = nullptr;
    };

    int32_t mRecordingDeviceId = oboe::kUnspecified;
    static oboe::AudioFormat constexpr mFormat = oboe::AudioFormat::Float;
    int32_t mSampleRate = oboe::kUnspecified;
    static constexpr int32_t mChannelCount = oboe::ChannelCount::Mono;

    std::mutex mLock;
    std::shared_ptr<oboe::AudioStream> mRecordingStream;
    std::shared_ptr<oboe::AudioStream> mPlaybackStream;

    std::unique_ptr<InputCallback> mInputCallback;
    std::unique_ptr<OutputCallback> mOutputCallback;


    static int constexpr    kSampleRate = 48000;
    static float constexpr  kAmplitude = 0.5f;
    static float constexpr  kFrequency = 440;
    static float constexpr  kPI = M_PI;
    static float constexpr  kTwoPi = kPI * 2;
    static double constexpr mPhaseIncrement = kFrequency * kTwoPi / (double) kSampleRate;
    // Keeps track of where the wave is




};





#endif //PIANO_TEACHER_AUDIOENGINE_H

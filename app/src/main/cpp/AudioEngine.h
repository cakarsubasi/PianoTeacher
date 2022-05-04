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
#include <array>
#include "SoundRecording.h"
//#include "Callbacks.h"

class AudioEngine {
public:
    AudioEngine();
    virtual ~AudioEngine() = default;

    bool start();
    void stop();
    void restart();
    void setPlaying(bool isPlaying);
    void setRecording(bool isRecording);

    float avgamplitude = 0.0;
    std::shared_ptr<SoundRecording> mAudioRecording = std::make_shared<SoundRecording>();
    //SoundRecording mAudioRecording;



    float amplitude();

private:
    static void convertArrayMonoToStereo(float *data, int32_t numFrames) {

        for (int i = numFrames - 1; i >= 0; i--) {
            data[i*2] = data[i];
            data[(i*2)+1] = data[i];
        }
    }

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
        InputCallback(std::shared_ptr<SoundRecording> &recording) {
            this->recording = recording;
        }

        oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream,
                                              void *audioData,
                                              int32_t numFrames) override {
            if (mIsRecording) {
                int32_t framesWritten = recording->write(static_cast<float*>(audioData), numFrames);
                if (framesWritten == 0) mIsRecording = false;
            }

            return oboe::DataCallbackResult::Continue;
        }
        bool mIsRecording = false;
    private:
        std::shared_ptr<SoundRecording> recording;
    };

    class OutputCallback: public oboe::AudioStreamDataCallback {
    public:
        OutputCallback(std::shared_ptr<SoundRecording> &recording) {
            this->recording = recording;
        }

        oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream,
                                              void *audioData,
                                              int32_t numFrames) override {
            if (mIsPlaying) {
                int32_t framesRead = recording->read(static_cast<float*>(audioData), numFrames);
                convertArrayMonoToStereo(static_cast<float*>(audioData), framesRead);
                if (framesRead < numFrames) mIsPlaying = false;
            }
            //float *floatData = (float *) audioData;
            //sineWave(floatData, numFrames, mPhase);

            return oboe::DataCallbackResult::Continue;
        }

        bool mIsPlaying = false;
    private:
        float mPhase = 0.0;
        std::shared_ptr<SoundRecording> recording;
    };


    int32_t mRecordingDeviceId = oboe::kUnspecified;
    static oboe::AudioFormat constexpr mFormat = oboe::AudioFormat::Float;
    int32_t mSampleRate = oboe::kUnspecified;
    static constexpr int32_t mChannelCount = oboe::ChannelCount::Mono;

    std::mutex mLock;
    std::shared_ptr<oboe::AudioStream> mRecordingStream;
    std::shared_ptr<oboe::AudioStream> mPlaybackStream;

    //static size_t constexpr buffersize = 8192;
    //std::shared_ptr<std::array<float, buffersize>> audioBuffer = std::make_shared<std::array<float, buffersize>>();

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

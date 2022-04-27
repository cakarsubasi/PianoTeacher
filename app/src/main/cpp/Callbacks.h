#ifndef PIANO_TEACHER_CALLBACKS_H
#define PIANO_TEACHER_CALLBACKS_H

#include <oboe/oboe.h>

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

        return oboe::DataCallbackResult::Continue;
    }
private:
    float mPhase = 0.0;
    oboe::AudioStream *mStream = nullptr;
};

#endif
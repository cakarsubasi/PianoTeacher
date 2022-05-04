#ifndef PIANO_TEACHER_CALLBACKS_H
#define PIANO_TEACHER_CALLBACKS_H

#include <oboe/oboe.h>
#include <array>

class InputCallback: public oboe::AudioStreamDataCallback {
public:
    static constexpr size_t buffersize = 8192;
    int32_t pos = 0;

    std::shared_ptr<std::array<float, buffersize>> buffer;

    InputCallback(std::shared_ptr<std::array<float, buffersize>> bufferptr) {
        buffer = bufferptr;
        //buffer.fill(0.0);
    }


    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream,
                                          void *audioData,
                                          int32_t numFrames) override {

        for (int i = 0; i < numFrames; ++i) {
            if (pos == buffersize) {
                pos = 0;
            }
            buffer->at(pos++) = *((static_cast<float*>(audioData))+i);
        }
        return oboe::DataCallbackResult::Continue;
    }

};

class OutputCallback: public oboe::AudioStreamDataCallback {
public:
    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *oboeStream,
                                          void *audioData,
                                          int32_t numFrames) override {

        //float *floatData = (float *) audioData;

        return oboe::DataCallbackResult::Continue;
    }
private:
    float mPhase = 0.0;
    oboe::AudioStream *mStream = nullptr;
};

#endif
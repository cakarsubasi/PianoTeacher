//
// Created by explo on 2022-04-26.
//

#include "AudioEngine.h"
#include <oboe/oboe.h>
#include <android/log.h>

void stopStream(std::shared_ptr<oboe::AudioStream> stream);

AudioEngine::AudioEngine()
    : mOutputCallback(std::make_unique<OutputCallback>()),
    mInputCallback(std::make_unique<InputCallback>(audioBuffer)) {
}

bool AudioEngine::start() {

    oboe::AudioStreamBuilder inBuilder;
    inBuilder.setDirection(oboe::Direction::Input)
    ->setChannelCount(mChannelCount)
    ->setSharingMode(oboe::SharingMode::Exclusive)
    ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
    ->setSampleRate(kSampleRate); /*
    ->setDataCallback(mInputCallback.get())
    ->setFramesPerCallback(512);
    */

    oboe::Result result = inBuilder.openStream(mRecordingStream);
    if (result != oboe::Result::OK) {
        __android_log_print(ANDROID_LOG_ERROR,
                            "AudioEngine",
                            "Error opening stream %s",
                            convertToText(result));
        return false;
    }

    __android_log_print(ANDROID_LOG_DEBUG,
                        "AudioEngine",
                        "Successfully opened input stream.");

    mRecordingStream->requestStart();

    oboe::AudioStreamBuilder outBuilder;
    outBuilder.setDirection(oboe::Direction::Output)
    ->setChannelCount(mChannelCount)
    ->setSharingMode(oboe::SharingMode::Exclusive)
    ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
    ->setSampleRate(mRecordingStream->getSampleRate())
    ->setDataCallback(mOutputCallback.get())
    ->setFramesPerCallback(512);

    result = outBuilder.openStream(mPlaybackStream);
    if (result != oboe::Result::OK) {
        __android_log_print(ANDROID_LOG_ERROR,
                            "AudioEngine",
                            "Error opening stream %s",
                            convertToText(result));
        return false;
    }

    //mPlaybackStream->requestStart();

    __android_log_print(ANDROID_LOG_DEBUG,
                        "AudioEngine",
                        "Successfully opened output stream.");


    return true;
}

void AudioEngine::stop() {
    stopStream(mRecordingStream);
    stopStream(mPlaybackStream);
}

void stopStream(std::shared_ptr<oboe::AudioStream> stream) {
    if (stream) {
        oboe::Result result = stream->stop();
        if (result != oboe::Result::OK) {
            __android_log_print(ANDROID_LOG_WARN,
                                "AudioEngine",
                                "Error stopping stream: %s",
                                convertToText(result));
        }
        result = stream->close();
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
        stream.reset();
    }
}

void AudioEngine::restart() {
    // TODO

}

float AudioEngine::amplitude() {
    oboe::Result result = mRecordingStream->read(audioBuffer->data(), 8192, 1000000);
    if (static_cast<int>(result) < 0) {
        __android_log_print(ANDROID_LOG_ERROR,
                            "SongActivityDebug",
                            "Failed read");
    }
    if (static_cast<int>(result) <= 8192) {
        __android_log_print(ANDROID_LOG_DEBUG,
                            "SongActivityDebug",
                            "Read %d frames",
                            result);
    }

    /*
    float total = 0.0;
    float entry = 0.0;
    for (int i = 0; i < audioBuffer->size(); ++i) {
        entry = audioBuffer->at(i);
        total += entry*entry;
    }
    __android_log_print(ANDROID_LOG_DEBUG,
                        "SongActivityDebug",
                        "amplitude %f", total/buffersize);
    return total / (float) audioBuffer->size();
     */
}

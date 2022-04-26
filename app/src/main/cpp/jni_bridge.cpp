#include <jni.h>
#include <string>
#include "AudioEngine.h"

static AudioEngine engine;

extern "C" {

JNIEXPORT jstring JNICALL
Java_com_kocuni_pianoteacher_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT jboolean JNICALL
Java_com_kocuni_pianoteacher_MainActivity_startEngine(
        JNIEnv* env,
        jobject) {

    engine.start();
    return true;
}

/*
JNIEXPORT void JNICALL

*/
} // extern "C"
extern "C"
JNIEXPORT void JNICALL
Java_com_kocuni_pianoteacher_MainActivity_setRecording(JNIEnv *env, jobject thiz,
                                                       jboolean is_recording) {
    // TODO: implement setRecording()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kocuni_pianoteacher_MainActivity_setPlaying(JNIEnv *env, jobject thiz,
                                                     jboolean is_playing) {
    // TODO: implement setPlaying()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kocuni_pianoteacher_MainActivity_stopEngine(JNIEnv *env, jobject thiz) {
    // TODO: implement stopEngine()
    engine.stop();
}
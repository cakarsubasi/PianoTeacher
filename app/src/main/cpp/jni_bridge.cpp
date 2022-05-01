#include <jni.h>
#include <string>
#include "AudioEngine.h"

static AudioEngine engine;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_kocuni_pianoteacher_JNIBridge_stringFromJNI(JNIEnv *env, jobject thiz) {
    // TODO: implement stringFromJNI()
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_kocuni_pianoteacher_JNIBridge_startEngine(JNIEnv *env, jobject thiz) {
    if(engine.start()) {
        return true;
    } else {
        return false;
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kocuni_pianoteacher_JNIBridge_stopEngine(JNIEnv *env, jobject thiz) {
    engine.stop();
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kocuni_pianoteacher_JNIBridge_setRecording(JNIEnv *env, jobject thiz,
                                                    jboolean is_recording) {
    // TODO: implement setRecording()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_kocuni_pianoteacher_JNIBridge_setPlaying(JNIEnv *env, jobject thiz, jboolean is_playing) {
    // TODO: implement setPlaying()
}
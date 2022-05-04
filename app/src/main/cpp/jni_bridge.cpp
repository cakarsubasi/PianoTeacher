#include <jni.h>
#include <string>
#include "AudioEngine.h"
#include <android/log.h>

static AudioEngine engine;
int RIDICULOUS_INT = 0;

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

extern "C"
JNIEXPORT jfloat JNICALL
Java_com_kocuni_pianoteacher_JNIBridge_marshallTest(JNIEnv *env, jobject thiz) {
    // TODO: implement marshallTest()

    return engine.amplitude();
}
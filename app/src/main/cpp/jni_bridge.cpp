#include <jni.h>
#include <string>
#include "AudioEngine.h"

static AudioEngine *engine = nullptr;

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
    if (engine == nullptr) {
        engine = new AudioEngine();
    }

    return (engine != nullptr) ? JNI_TRUE : JNI_FALSE;
}


} // extern "C"
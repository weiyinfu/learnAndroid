/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_example_soundtouchdemo_JNISoundTouch */
#ifndef _Included_com_example_soundtouchdemo_JNISoundTouch
#define _Included_com_example_soundtouchdemo_JNISoundTouch
#ifdef __cplusplus
extern "C" {
#endif

#include "SoundTouch/SoundTouch.h"
#define BUFFER_SIZE 4096

soundtouch::SoundTouch mSoundTouch;


/*
 * Class:     com_example_soundtouchdemo_JNISoundTouch
 * Method:    setSampleRate
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_example_soundtouchdemo_JNISoundTouch_setSampleRate
  (JNIEnv *env, jobject obj, jint sampleRate)
{
	mSoundTouch.setSampleRate(sampleRate);
}

/*
 * Class:     com_example_soundtouchdemo_JNISoundTouch
 * Method:    setChannels
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_example_soundtouchdemo_JNISoundTouch_setChannels
  (JNIEnv *env, jobject obj, jint channel)
{
	mSoundTouch.setChannels(channel);
}

/*
 * Class:     com_example_soundtouchdemo_JNISoundTouch
 * Method:    setTempoChange
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_com_example_soundtouchdemo_JNISoundTouch_setTempoChange
  (JNIEnv *env, jobject obj, jfloat newTempo)
{
	mSoundTouch.setTempoChange(newTempo);
}

/*
 * Class:     com_example_soundtouchdemo_JNISoundTouch
 * Method:    setPitchSemiTones
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_example_soundtouchdemo_JNISoundTouch_setPitchSemiTones
  (JNIEnv *env, jobject obj, jint pitch)
{
	mSoundTouch.setPitchSemiTones(pitch);
}

/*
 * Class:     com_example_soundtouchdemo_JNISoundTouch
 * Method:    setRateChange
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_com_example_soundtouchdemo_JNISoundTouch_setRateChange
  (JNIEnv *env, jobject obj, jfloat newRate)
{
	mSoundTouch.setRateChange(newRate);
}

/*
 * Class:     com_example_soundtouchdemo_JNISoundTouch
 * Method:    putSamples
 * Signature: ([SI)V
 */
JNIEXPORT void JNICALL Java_com_example_soundtouchdemo_JNISoundTouch_putSamples
  (JNIEnv *env, jobject obj, jshortArray samples, jint len)
{
	// 转换为本地数组
	jshort *input_samples = env->GetShortArrayElements(samples, NULL);
	mSoundTouch.putSamples(input_samples, len);
	// 释放本地数组(避免内存泄露)
	env->ReleaseShortArrayElements(samples, input_samples, 0);
}

/*
 * Class:     com_example_soundtouchdemo_JNISoundTouch
 * Method:    receiveSamples
 * Signature: ([SI)I
 */
JNIEXPORT jshortArray JNICALL Java_com_example_soundtouchdemo_JNISoundTouch_receiveSamples
  (JNIEnv *env, jobject obj)
{
	short buffer[BUFFER_SIZE];
	int nSamples = mSoundTouch.receiveSamples(buffer, BUFFER_SIZE);

	// 局部引用，创建一个short数组
	jshortArray receiveSamples = env->NewShortArray(nSamples);
	// 给short数组设置值
	env->SetShortArrayRegion(receiveSamples, 0, nSamples, buffer);

	return receiveSamples;
}

#ifdef __cplusplus
}
#endif
#endif


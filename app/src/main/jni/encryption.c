#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_github_vladkraievskiy_test_1np_encryption_Encryption_getRsaAlias(JNIEnv *env,
                                                                          jclass type) {
    return (*env)->NewStringUTF(env, "RSA alias");
}

JNIEXPORT jstring JNICALL
Java_com_github_vladkraievskiy_test_1np_encryption_Encryption_getProviderType(JNIEnv *env,
                                                                              jclass type) {
    return (*env)->NewStringUTF(env, "AndroidKeyStore");
}

JNIEXPORT jint JNICALL
Java_com_github_vladkraievskiy_test_1np_encryption_Encryption_getRsaKeySize(JNIEnv *env,
                                                                            jclass type) {

    return 2048;
}

JNIEXPORT jint JNICALL
Java_com_github_vladkraievskiy_test_1np_encryption_Encryption_getAesKeySize(JNIEnv *env,
                                                                            jclass type) {
    return 256;
}

JNIEXPORT jint JNICALL
Java_com_github_vladkraievskiy_test_1np_encryption_Encryption_getIvSize(JNIEnv *env, jclass type) {
    return 12;
}

JNIEXPORT jint JNICALL
Java_com_github_vladkraievskiy_test_1np_encryption_Encryption_getGcmTagLength(JNIEnv *env,
                                                                              jclass type) {
    return 128;
}

JNIEXPORT jstring JNICALL
Java_com_github_vladkraievskiy_test_1np_encryption_Encryption_getRsaCipher(JNIEnv *env,
                                                                           jclass type) {
    return (*env)->NewStringUTF(env, "RSA/ECB/PKCS1Padding");
}

JNIEXPORT jstring JNICALL
Java_com_github_vladkraievskiy_test_1np_encryption_Encryption_getAesCipher(JNIEnv *env,
                                                                           jclass type) {
    return (*env)->NewStringUTF(env, "AES/GCM/NoPadding");
}

JNIEXPORT jstring JNICALL
Java_com_github_vladkraievskiy_test_1np_encryption_Encryption_getSignatureAlgorithm(JNIEnv *env,
                                                                                    jclass type) {
    return (*env)->NewStringUTF(env, "SHA256withRSA");
}
#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace cv;
extern "C" JNIEXPORT jstring

JNICALL
Java_com_gmail_roquen4145_khdd_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_gmail_roquen4145_khdd_MainActivity_ConvertRGBtoGray(JNIEnv *env, jobject instance,
//                                                             jlong matAddrInput,
//                                                             jlong matAddrResult) {
//
//    // TODO
//    Mat &matInput = *(Mat *)matAddrInput;
//    Mat &matResult = *(Mat *)matAddrResult;
//
//    cvtColor(matInput, matResult, CV_RGBA2GRAY);
//}

extern "C"
JNIEXPORT void JNICALL
Java_com_gmail_roquen4145_khdd_MainActivity_process(JNIEnv *env, jobject instance,
                                                    jlong matAddrInput, jlong matAddrResult) {

    // TODO

    Mat &matInput = *(Mat *)matAddrInput;
    Mat &matResult = *(Mat *)matAddrResult;

    cvtColor(matInput, matResult, CV_RGBA2GRAY);

}
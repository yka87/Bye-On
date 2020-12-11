#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include "android/bitmap.h"
#include <stdio.h>

/*
 * This library provides functions to convert bitmap to mat and vice versa,
 * and process images using the openCV C++ library.
 * Code from and based on the openCV repo [https://github.com/opencv/opencv]
 */
using namespace cv;
extern "C" {

JNIEXPORT void JNICALL
Java_com_example_opencv_ImageActivity_nBitmapToMat (JNIEnv * env, jobject thiz, jobject bitmap, jlong m_addr, jboolean needUnPremultiplyAlpha)
    {
    AndroidBitmapInfo info;
    void *pixels = 0;
    Mat &dst = *((Mat *) m_addr);

    try {
        CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
        CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                  info.format == ANDROID_BITMAP_FORMAT_RGB_565);
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
        CV_Assert(pixels);
        dst.create(info.height, info.width, CV_8UC4);
        if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if (needUnPremultiplyAlpha) cvtColor(tmp, dst, COLOR_mRGBA2RGBA);
            else tmp.copyTo(dst);
        } else {
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            cvtColor(tmp, dst, COLOR_BGR5652RGBA);
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    } catch (const cv::Exception &e) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nBitmapToMat}");
        return;
    }
}

JNIEXPORT void JNICALL Java_com_example_opencv_ImageActivity_nMatToBitmap (JNIEnv * env, jobject, jlong m_addr, jobject bitmap, jboolean needPremultiplyAlpha) {
    AndroidBitmapInfo info;
    void *pixels = 0;
    Mat&               src = *((Mat*)m_addr);

    try {
        CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
        CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                  info.format == ANDROID_BITMAP_FORMAT_RGB_565);
        CV_Assert(src.dims == 2 && info.height == (uint32_t) src.rows &&
                  info.width == (uint32_t) src.cols);
        CV_Assert(src.type() == CV_8UC1 || src.type() == CV_8UC3 || src.type() == CV_8UC4);
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
        CV_Assert(pixels);
        if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if (src.type() == CV_8UC1) {
                cvtColor(src, tmp, COLOR_GRAY2RGBA);
            } else if (src.type() == CV_8UC3) {
                cvtColor(src, tmp, COLOR_RGB2RGBA);
            } else if (src.type() == CV_8UC4) {
                if (needPremultiplyAlpha) cvtColor(src, tmp, COLOR_RGBA2mRGBA);
                else src.copyTo(tmp);
            }
        } else {
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            if (src.type() == CV_8UC1) {
                cvtColor(src, tmp, COLOR_GRAY2BGR565);
            } else if (src.type() == CV_8UC3) {
                cvtColor(src, tmp, COLOR_RGB2BGR565);
            } else if (src.type() == CV_8UC4) {
                cvtColor(src, tmp, COLOR_RGBA2BGR565);
            }
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    } catch (const cv::Exception &e) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
        return;
    }
}

// Takes care of image processing such as
// adding pet name on the image, letting user choose the texture of the image, and resizing the image etc.
JNIEXPORT void JNICALL Java_com_example_opencv_ImageActivity_processImage
(JNIEnv *env, jobject thiz, jlong input_image, jlong output_image, jint th1, jint th2, jstring name) {

    //Connect address of Java Mat to the Mat object of native-lib.cpp
    Mat &img_input = *(Mat *) input_image;
    Mat &img_output = *(Mat *) output_image;

    cvtColor(img_input, img_output, COLOR_BGR2BGRA);
    GaussianBlur(img_input, img_output, Size( th1, th1 ), 0, 0);
//        bilateralFilter(img_output, img_output, 15, 20, 20);

    jboolean isCopy;
    const char *input_name = (env)->GetStringUTFChars(name, &isCopy);

    //    https://docs.opencv.org/master/d4/d86/group__imgproc__filter.html
    putText(img_output, input_name, Point(20,70), FONT_HERSHEY_SCRIPT_SIMPLEX,
            3, Scalar(0,143,143), 2);

    //Code found at [https://stackoverflow.com/questions/28562401/resize-an-image-to-a-square-but-keep-aspect-ratio-c-opencv]
    int desWidth = 1024;
    int desHeight = 768;
    double h1 = desWidth * (img_output.rows/(double)img_output.cols);
    double w1 = desHeight * (img_output.cols/(double)img_output.rows);
    if( h1 <= desHeight) {
        resize(img_output, img_output, cv::Size(desWidth, h1));
    } else {
        resize(img_output, img_output, cv::Size(w1, desHeight));
    }
//
    int top = (desHeight-img_output.rows) / 2;
    int down = (desHeight-img_output.rows+1) / 2;
    int left = (desWidth - img_output.cols) / 2;
    int right = (desWidth - img_output.cols+1) / 2;

    copyMakeBorder(img_output, img_output, top, down, left, right, cv::BORDER_WRAP);
}

}
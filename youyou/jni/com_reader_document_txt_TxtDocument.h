/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_reader_document_txt_TxtDocument */

#ifndef _Included_com_reader_document_txt_TxtDocument
#define _Included_com_reader_document_txt_TxtDocument
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_reader_document_txt_TxtDocument
 * Method:    pageCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_txt_TxtDocument_pageCount
  (JNIEnv *, jobject);

/*
 * Class:     com_reader_document_txt_TxtDocument
 * Method:    loadDocument
 * Signature: (Ljava/lang/String;II)I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_txt_TxtDocument_loadDocument
  (JNIEnv *, jobject, jstring, jint, jint);

/*
 * Class:     com_reader_document_txt_TxtDocument
 * Method:    getPage
 * Signature: (ILandroid/graphics/Bitmap;)I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_txt_TxtDocument_getPage
  (JNIEnv *, jobject, jint,jobject);

#ifdef __cplusplus
}
#endif
#endif
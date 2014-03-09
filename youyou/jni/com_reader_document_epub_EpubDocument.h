/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_reader_document_epub_EpubDocument */

#ifndef _Included_com_reader_document_epub_EpubDocument
#define _Included_com_reader_document_epub_EpubDocument
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_reader_document_epub_EpubDocument
 * Method:    pageCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_epub_EpubDocument_pageCount
  (JNIEnv *, jobject);

/*
 * Class:     com_reader_document_epub_EpubDocument
 * Method:    loadDocument
 * Signature: (Ljava/lang/String;II)I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_epub_EpubDocument_loadDocument
  (JNIEnv *, jobject, jstring, jint, jint);

/*
 * Class:     com_reader_document_epub_EpubDocument
 * Method:    getPage
 * Signature: (Lcom/reader/document/epub/EpubPageAddr;Landroid/graphics/Bitmap;)I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_epub_EpubDocument_getPage
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     com_reader_document_epub_EpubDocument
 * Method:    nextPageAddr
 * Signature: (Lcom/reader/document/epub/EpubPageAddr;)Lcom/reader/document/epub/EpubPageAddr;
 */
JNIEXPORT jobject JNICALL Java_com_reader_document_epub_EpubDocument_nextPageAddr
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_reader_document_epub_EpubDocument
 * Method:    prevPageAddr
 * Signature: (Lcom/reader/document/epub/EpubPageAddr;)Lcom/reader/document/epub/EpubPageAddr;
 */
JNIEXPORT jobject JNICALL Java_com_reader_document_epub_EpubDocument_prevPageAddr
  (JNIEnv *, jobject, jobject);

#ifdef __cplusplus
}
#endif
#endif

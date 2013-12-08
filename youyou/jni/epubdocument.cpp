/*
 * epubdocument.cpp
 *
 *  Created on: 2013Äê12ÔÂ7ÈÕ
 *      Author: song
 */

#include <jni.h>
#include <com_reader_document_epub_EpubDocument.h>
#include <vector>
#include <assert.h>
#include "cr3java.h"
#include "lvstring.h"
#include "lvstream.h"
#include "crtxtenc.h"
#include "cssdef.h"
#include "lvthread.h"
#include "epubfmt.h"

/*
 * Class:     com_reader_document_epub_EpubDocument
 * Method:    pageCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_epub_EpubDocument_pageCount(
		JNIEnv *env, jobject self);
EpubDocument epub;
/*
 * Class:     com_reader_document_epub_EpubDocument
 * Method:    loadDocument
 * Signature: (Ljava/lang/String;II)I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_epub_EpubDocument_loadDocument(
		JNIEnv *e, jobject self, jstring bookPath, jint w, jint h) {
	CRJNIEnv env(e);
	lString16 path = env.fromJavaString(bookPath);
	LVStreamRef stream = LVOpenFileStream(path.c_str(), LVOM_READ);

	epub.loadDocument(stream);
	epub.Render(w,h);
	return 0;
}

/*
 * Class:     com_reader_document_epub_EpubDocument
 * Method:    getPage
 * Signature: (ILandroid/graphics/Bitmap;)I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_epub_EpubDocument_getPage(
		JNIEnv *e, jobject self, jint index, jobject bitmap) {
	CRJNIEnv env(e);
	LVDrawBuf * drawbuf = BitmapAccessorInterface::getInstance()->lock(e,
			bitmap);
	if (drawbuf != NULL) {
		drawbuf->FillRect(0, 0, epub.getWidth(), epub.getHeight(), 0x00ffeeee);
		drawbuf->SetTextColor(0x00000000);
		//		txt_book->drawPage(drawbuf, index);
		epub.Draw(*drawbuf, index);
		//CRLog::trace("getPageImageInternal calling bitmap->unlock");
		BitmapAccessorInterface::getInstance()->unlock(e, bitmap, drawbuf);
	} else {
		CRLog::error("bitmap accessor is invalid");
	}
	return 0;
}

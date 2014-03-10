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
EpubDocument epub;
class C_EpubAddr {
	jobject _java_obj;
public:
	C_EpubAddr(jobject o):_java_obj(o) {

	}
	jobject EpubDocument() {
		jclass cls = GET_ENV()->FindClass("com/reader/document/epub/EpubPageAddr");
		jfieldID field =GET_ENV()->GetFieldID(cls,"_epub","Lcom/reader/document/epub/EpubDocument;");
		return GET_ENV()->GetObjectField(_java_obj,field);
	}
	int chapterIndex() {
		jclass cls = GET_ENV()->FindClass("com/reader/document/epub/EpubPageAddr");
		jfieldID field =GET_ENV()->GetFieldID(cls,"_chapter_index","I");
		return GET_ENV()->GetIntField(_java_obj,field);
	}
	int pageIndex() {
		jclass cls = GET_ENV()->FindClass("com/reader/document/epub/EpubPageAddr");
		jfieldID field =GET_ENV()->GetFieldID(cls,"_page_index","I");
		return GET_ENV()->GetIntField(_java_obj,field);
	}
	void setChapterIndex(int index) {
		jclass cls = GET_ENV()->FindClass("com/reader/document/epub/EpubPageAddr");
		jfieldID field =GET_ENV()->GetFieldID(cls,"_chapter_index","I");
		GET_ENV()->SetIntField(_java_obj,field,index);
	}
	void setPageIndex(int index) {
		jclass cls = GET_ENV()->FindClass("com/reader/document/epub/EpubPageAddr");
		jfieldID field =GET_ENV()->GetFieldID(cls,"_page_index","I");
		GET_ENV()->SetIntField(_java_obj,field,index);
	}
	static jobject NewObject(jobject epubdocument) {
		jclass cls = GET_ENV()->FindClass("com/reader/document/epub/EpubPageAddr");
		jmethodID method = GET_ENV()->GetMethodID(cls,"<init>","(Lcom/reader/document/epub/EpubDocument;)V");
		return GET_ENV()->NewObject(cls,method,epubdocument);
	}
};
/*
 * Class:     com_reader_document_epub_EpubDocument
 * Method:    pageCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_epub_EpubDocument_pageCount(
		JNIEnv *env, jobject self) {
	return epub.getPageCount();
}

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
 * Signature: (Lcom/reader/document/epub/EpubPageAddr;Landroid/graphics/Bitmap;)I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_epub_EpubDocument_getPage
  (JNIEnv *e, jobject self, jobject jEpubAddr, jobject bitmap)
{
	SET_ENV(e);
	C_EpubAddr addr(jEpubAddr);
	CRJNIEnv env(e);
	LVDrawBuf * drawbuf = BitmapAccessorInterface::getInstance()->lock(e,
			bitmap);
	if (drawbuf != NULL) {
		drawbuf->FillRect(0, 0, epub.getWidth(), epub.getHeight(), 0x00ffeeee);
		drawbuf->SetTextColor(0x00000000);
		//		txt_book->drawPage(drawbuf, index);
		epub.Draw(*drawbuf, addr.chapterIndex(),addr.pageIndex());
		//CRLog::trace("getPageImageInternal calling bitmap->unlock");
		BitmapAccessorInterface::getInstance()->unlock(e, bitmap, drawbuf);
	} else {
		CRLog::error("bitmap accessor is invalid");
	}
	return 0;
}
/*
 * Class:     com_reader_document_epub_EpubDocument
 * Method:    nextPageAddr
 * Signature: (Lcom/reader/document/epub/EpubPageAddr;)Lcom/reader/document/epub/EpubPageAddr;
 */
JNIEXPORT jobject JNICALL Java_com_reader_document_epub_EpubDocument_nextPageAddr
  (JNIEnv *e, jobject self, jobject jCur) {
	SET_ENV(e);
	C_EpubAddr c_cur(jCur);
	jobject jNext = C_EpubAddr::NewObject(c_cur.EpubDocument());
	C_EpubAddr c_next(jNext);
	DocumentPage &p = epub.mDocumentPages[c_cur.chapterIndex()];
	if (c_cur.pageIndex() < p.m_pages.length()-1) {
		c_next.setChapterIndex(c_cur.chapterIndex());
		c_next.setPageIndex(c_cur.pageIndex()+1);
	} else {
		if (((unsigned int)c_cur.chapterIndex()+1) < epub.mDocumentPages.size()) {
			c_next.setChapterIndex(c_cur.chapterIndex()+1);
			c_next.setPageIndex(0);
		} else {
			c_next.setChapterIndex(c_cur.chapterIndex());
			c_next.setPageIndex(c_cur.pageIndex());
		}
	}
	return jNext;
}

/*
 * Class:     com_reader_document_epub_EpubDocument
 * Method:    prevPageAddr
 * Signature: (Lcom/reader/document/epub/EpubPageAddr;)Lcom/reader/document/epub/EpubPageAddr;
 */
JNIEXPORT jobject JNICALL Java_com_reader_document_epub_EpubDocument_prevPageAddr
  (JNIEnv *env, jobject self, jobject jCur)
{
return jCur;
}

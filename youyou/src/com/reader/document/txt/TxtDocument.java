package com.reader.document.txt;

import android.graphics.Bitmap;
//javah -classpath bin\classes -jni com.reader.document.txt.TxtDocument
//
public class TxtDocument {
	static {
		System.loadLibrary("cr3engine-3-1-0");
	}
	public native int pageCount();
	public native int loadDocument(String bookPath, int width, int height);
	public PageDrawable getPageDrawble() {
		return null;
	}
	public native int getPage(Bitmap b);
}

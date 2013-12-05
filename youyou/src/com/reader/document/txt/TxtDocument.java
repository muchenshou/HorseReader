package com.reader.document.txt;

import android.graphics.Bitmap;
//javah -classpath bin\classes -jni com.reader.document.txt.TxtDocument
//
public class TxtDocument {
	
	public native int pageCount();
	public native int loadDocument(String bookPath, int width, int height);
	public PageDrawable getPageDrawble() {
		return null;
	}
	public synchronized native int getPage(int index, Bitmap b);
}

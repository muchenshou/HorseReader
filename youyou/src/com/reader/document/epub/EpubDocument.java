package com.reader.document.epub;

import android.graphics.Bitmap;

public class EpubDocument {
	public native int pageCount();
	public native int loadDocument(String bookPath, int width, int height);
	public synchronized native int getPage(int index, Bitmap b);
}

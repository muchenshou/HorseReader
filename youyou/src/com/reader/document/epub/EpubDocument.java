package com.reader.document.epub;

import android.graphics.Bitmap;

public class EpubDocument {
	public native int pageCount();
	public native int loadDocument(String bookPath, int width, int height);
//	public synchronized int getPage(int index, Bitmap b) {
//		return 0;
//	}
	public synchronized native int getPage(EpubPageAddr curPageAddr,Bitmap b);
	public native EpubPageAddr nextPageAddr(EpubPageAddr curPageAddr);
	public native EpubPageAddr prevPageAddr(EpubPageAddr curPageAddr);
	public synchronized native int setBg(byte[] imageBytes);
}

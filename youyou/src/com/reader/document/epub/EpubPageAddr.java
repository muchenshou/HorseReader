package com.reader.document.epub;

import android.util.Log;

public class EpubPageAddr{
	int _chapter_index;
	int _page_index;
	EpubDocument _epub;
	public EpubPageAddr(EpubDocument d) {
		// TODO Auto-generated constructor stub
		_epub = d;
	}
	EpubPageAddr next() {
		return _epub.nextPageAddr(this);
	}
	
	EpubPageAddr pre() {
		return _epub.prevPageAddr(this);
	}

	@Override
	public boolean equals(Object o) {
		Log.i("song", "song  equals epubaddr");
		EpubPageAddr another = (EpubPageAddr)o;
		if (_chapter_index == another._chapter_index &&
				_page_index == another._page_index)
			return true;
		return false;
	}
	@Override
	public int hashCode() {
		Log.i("song", "song  hashCode epubaddr");
		return _chapter_index<<16+_page_index;
	}
	
	
}

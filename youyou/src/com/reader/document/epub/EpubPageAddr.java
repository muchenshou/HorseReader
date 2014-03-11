package com.reader.document.epub;

import android.util.Log;


public class EpubPageAddr{
	int _chapter_index;
	int _page_index;
	EpubDocument _epub;
	public EpubPageAddr(EpubDocument d) {
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
		Log.i("song","equals");
		return this.hashCode() == o.hashCode();
	}
	@Override
	public int hashCode() {
		return _chapter_index<<16+_page_index;
	}
	
}

package com.reader.document.epub;

public class EpubPageAddr implements Comparable<EpubPageAddr>{
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
	public int compareTo(EpubPageAddr another) {
		if (_chapter_index == another._chapter_index &&
				_page_index == another._page_index)
		return 0;
		return 1;
	}
}

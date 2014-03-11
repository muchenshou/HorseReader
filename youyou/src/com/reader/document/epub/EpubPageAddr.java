package com.reader.document.epub;


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
	public int hashCode() {
		return _chapter_index<<16+_page_index;
	}
	
}

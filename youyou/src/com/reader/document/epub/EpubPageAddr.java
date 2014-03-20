package com.reader.document.epub;



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
		return this.hashCode() == o.hashCode();
	}
	@Override
	public int hashCode() {
//		Log.i("song","hashCode:"+_chapter_index+":"+_page_index+":"+((_chapter_index<<16)+_page_index));
		return ((_chapter_index<<16)+_page_index);
	}
	@Override
	public String toString() {
		return "chapter index:"+_chapter_index + "page index:"+_page_index;
	}
	
}

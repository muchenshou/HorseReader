package com.reader.book;

import java.util.LinkedList;

import com.reader.book.manager.BookPosition;

public class PageBuffer {
	LinkedList<Page> mPages = new LinkedList<Page>();
	Page mCurPage = null;
	final int PAGESIZE = 10;

	public Page existPage(BookPosition position) {
		return null;
	}

	public Page addPage(Page page) {
		mPages.add(page);
		return page;
	}

	public boolean isEmpty() {
		return mPages.size() == 0 ? true : false;
	}
}
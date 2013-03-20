package com.reader.book;

import java.util.LinkedList;

public class PageBuffer {
	LinkedList<Page> mPages = new LinkedList<Page>();
	Page mCurPage = null;
	final int PAGESIZE = 10;
	
	public Page existPage(int pos){
		for (Page p: mPages) {
			if (p.getPageStartPosition() == pos) {
				return p;
			}
		}
		return null;
	}
	
	public Page existPrePage(int pos) {
		for (Page p: mPages) {
			if (p.getPageEndPosition() + 1 == pos) {
				return p;
			}
		}
		return null;
	}
	
	public Page addPage(Page page) {
		mPages.add(page);
		while(mPages.size()>10) {
			for (int i = 0; i <mPages.size(); i++) {
				Page p = mPages.get(i);
				if (p != page && p != mCurPage) {
					mPages.remove(p);
					break;
				}
			}
		}
		return page;
	}
	
	public boolean isEmpty() {
		return mPages.size() == 0 ? true : false;
	}
}
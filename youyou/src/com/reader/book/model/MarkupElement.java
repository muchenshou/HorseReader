package com.reader.book.model;

import com.reader.book.Book;

public abstract class MarkupElement {
	enum TYPE {
		TEXT,
		IMAGE,
		NEWLINE
	}
	enum STATUS {
		
	}
	Cursor mElementCursor = new Cursor();
	public int index;
	TYPE type = TYPE.TEXT;
	Book mBook;
	public Cursor getElementCursor() {
		return mElementCursor;
	}
	public abstract int getLength();
	public int size() {
		return mElementCursor.mRealFileLast - mElementCursor.mRealFileStart + 1;
	}
	public abstract void fill();
	public interface Iterator{
		public boolean hasNext();
		public MarkupElement next();
	}
}

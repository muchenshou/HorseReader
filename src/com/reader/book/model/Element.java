package com.reader.book.model;

import com.reader.book.Book;

public abstract class Element {
	enum TYPE {
		TEXT,
		IMAGE
	}
	enum STATUS {
		
	}
	Cursor mElementCursor = new Cursor();
	public int index;
	Book mBook;
	public Cursor getElementCursor() {
		return mElementCursor;
	}
	public abstract int getLength();
	public abstract void fill();
	public interface Iterator{
		public boolean hasNext();
		public Element next();
	}
}

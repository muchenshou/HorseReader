package com.reader.book.model;

import com.reader.book.Book;

public abstract class Element {
	enum TYPE {
		TEXT,
		IMAGE
	}
	enum STATUS {
		
	}
	Cursor mRealFileStart = new Cursor();
	Cursor mRealFileLast = new Cursor();;
	int index;
	Book mBook;
	public abstract void fill();
	public interface Iterator{
		public boolean hasNext();
		public Element next();
	}
}

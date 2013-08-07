package com.reader.book.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.reader.book.AreaDraw;
import com.reader.book.Book;
import com.reader.book.Line;
import com.reader.book.Page;
import com.reader.book.manager.BookManager;

public abstract class MarkupElement {
	enum TYPE {
		TEXT, IMAGE, NEWLINE
	}

	enum STATUS {

	}

	public Cursor mElementCursor = new Cursor();
	public int index;
	TYPE type = TYPE.TEXT;
	Book mBook;

	public Cursor getElementCursor() {
		return mElementCursor;
	}

	public abstract int getLength();

	public abstract void fill();

	private static float flag = 0.0f;
	final float screenHeight = BookManager.View.getHeight() - 20;

	public void pushIntoLines(List<AreaDraw> lines,
			CopyOnWriteArrayList<Page> pages) {

	}

	abstract AreaDraw toDrawArea();
}

package com.reader.book.model;

import java.util.List;

import com.reader.book.AreaDraw;
import com.reader.book.Book;
import com.reader.book.Line;

public abstract class MarkupElement {
	enum TYPE {
		TEXT, IMAGE, NEWLINE
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

	public void pushIntoLines(List<AreaDraw> lines) {
		AreaDraw next;
		int offset = 0;
		fill();
		do {
			next = new Line(offset, this);
			next.fill();
			if ((next.offset + next.length) >= getLength()) {
				lines.add(next);
				return;
			} else {
				offset = next.offset + next.length;
			}
			lines.add(next);
		} while (true);
	}

}

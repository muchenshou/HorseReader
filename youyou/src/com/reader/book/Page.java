package com.reader.book;

import java.util.LinkedList;
import java.util.List;

import com.reader.book.manager.BookPosition;
import com.reader.book.model.BookModel;
import com.reader.book.model.Cursor;
import com.reader.book.model.MarkupElement;

public class Page implements Comparable<Page> {
	LinkedList<AreaDraw> mLines = new LinkedList<AreaDraw>();
	private BookModel mBookModel;

	public Page(BookModel model) {
		mBookModel = model;
	}

	public List<AreaDraw> getAreasDraw() {
		return mLines;
	}

	public boolean isNull() {
		return mLines.size() == 0;
	}

	public int getLinesSize() {
		return mLines.size();
	}

	public void clear() {
		mLines.clear();
	}

	public void addLine(AreaDraw line) {
		mLines.add(line);
	}

	@Override
	public int compareTo(Page another) {
		return 0;
	}

	public BookPosition getLastPos() {
		BookPosition pos = new BookPosition(0, 0, 0);
		final AreaDraw lastLine = mLines.getLast();
		final MarkupElement element = lastLine.element;
		final Cursor cursor = element.getElementCursor();
		pos.mElementIndex = element.index;
		pos.mOffset = lastLine.offset + lastLine.length;
		pos.mRealBookPos = cursor.getRealFileStart();
		return pos;
	}
}
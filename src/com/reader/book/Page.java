package com.reader.book;

import java.util.LinkedList;
import java.util.List;

import com.reader.book.bookview.BookView;
import com.reader.book.manager.BookPosition;
import com.reader.book.model.BookModel;
import com.reader.book.model.Cursor;
import com.reader.book.model.Element;

public class Page implements Comparable<Page> {
	LinkedList<AreaDraw> mLines = new LinkedList<AreaDraw>();
	public BookPosition mBookPosition;
	private BookModel mBookModel;
	public Page(BookModel model,BookPosition pos) {
		mBookModel = model;
		mBookPosition = pos;
	}
	public List<AreaDraw> getAreasDraw() {
		return mLines;
	}
	public boolean isNull () {
		return mLines.size() == 0;
	}

	public int getLinesSize() {
		return mLines.size();
	}

	public void clear() {
		mLines.clear();
	}

	public void addLine(Line line) {
		mLines.add(line);
	}

	@Override
	public int compareTo(Page another) {
		return 0;
	}

	public void fill() {
		final int pageheight = BookView.Instance.getHeight();
		int height = 0;
		AreaDraw pre;
		AreaDraw next;
		
		Element.Iterator iter = mBookModel.iterator(mBookPosition.mElementIndex, mBookPosition.mRealBookPos);
		Element element = iter.next();
		while(height < pageheight) {
			next = new Line(mBookPosition);
		}
		mLines.clear();
		
	}
	public BookPosition getLastPos() {
		BookPosition pos = new BookPosition(0, 0, 0);
		final AreaDraw lastLine = mLines.getLast();
		final Element element = lastLine.element;
		final Cursor cursor = element.getElementCursor();
		pos.mElementIndex = element.index;
		pos.mOffset = lastLine.offset+lastLine.lenght;
		pos.mRealBookPos = cursor.getRealFileStart();
		return pos;
	}
}
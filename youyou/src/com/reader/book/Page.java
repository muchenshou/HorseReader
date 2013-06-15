package com.reader.book;

import java.util.LinkedList;
import java.util.List;

import com.reader.book.bookview.BookView;
import com.reader.book.manager.BookPosition;
import com.reader.book.model.BookModel;
import com.reader.book.model.Cursor;
import com.reader.book.model.MarkupElement;

public class Page implements Comparable<Page> {
	LinkedList<AreaDraw> mLines = new LinkedList<AreaDraw>();
	public BookPosition mBookPosition;
	private BookModel mBookModel;

	public Page(BookModel model, BookPosition pos) {
		mBookModel = model;
		mBookPosition = pos;
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

	public void addLine(Line line) {
		mLines.add(line);
	}

	@Override
	public int compareTo(Page another) {
		return 0;
	}

	public void fill() {
		final int pageheight = BookView.Instance.getHeight() - 30;
		int height = 0;
		AreaDraw next;

		MarkupElement.Iterator iter = mBookModel.iterator(
				mBookPosition.mElementIndex, mBookPosition.mRealBookPos);
		MarkupElement element = iter.next();
		BookPosition pos = mBookPosition;
		do {
			next = new Line(pos,element);
			next.fill();
			if ((next.offset + next.length) >= element.getLength()) {
				pos = new BookPosition(pos);
				pos.mElementIndex += 1;
				pos.mOffset = 0;
				element = iter.next();
			} else {
				pos = new BookPosition(pos);
				pos.mOffset = next.offset + next.length;
			}
			height += next.getHeight();
			if (height < pageheight) {
				mLines.add(next);
				continue;
			}
			break;
		} while (true);

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
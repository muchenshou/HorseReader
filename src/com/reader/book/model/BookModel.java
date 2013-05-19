package com.reader.book.model;

import android.util.SparseArray;

import com.reader.book.Book;

public class BookModel {
	Book mBook;
	SparseArray<Element> mElements = new SparseArray<Element>();

	public BookModel(Book book) {
		mBook = book;
	}

	public class IteratorIml implements Element.Iterator {
		Element mCurrentElement;

		@Override
		public boolean hasNext() {
			return mCurrentElement.mElementCursor.getRealFileLast() < mBook
					.size() - 1;
		}

		@Override
		public Element next() {
			Element cur = mCurrentElement;
			mCurrentElement = new ParagraphElement(mBook);
			mCurrentElement.mElementCursor.mRealFileStart = cur.mElementCursor.mRealFileLast+1;
			mCurrentElement.index = cur.index + 1;
			mCurrentElement.fill();
			return cur;
		}
	}

	public Element.Iterator iterator(int elementIndex, int realfilepos) {
		// need to judge which type of element
		// default text element now
		Element element = new ParagraphElement(mBook);
		element.mElementCursor.setRealFileStart(realfilepos);
		element.index = elementIndex;
		element.fill();
		IteratorIml iter = new IteratorIml();
		iter.mCurrentElement = element;
		return iter;
	}
}

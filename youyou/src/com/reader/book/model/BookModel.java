package com.reader.book.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.reader.book.Book;

public class BookModel {
	Book mBook;
	BlockingQueue<MarkupElement> mElements = new LinkedBlockingQueue<MarkupElement>();

	public BookModel(Book book) {
		mBook = book;
	}

	public class IteratorIml implements MarkupElement.Iterator {
		MarkupElement mCurrentElement;

		@Override
		public boolean hasNext() {
			return mCurrentElement.mElementCursor.getRealFileLast() < mBook
					.size() - 1;
		}

		@Override
		public MarkupElement next() {
			MarkupElement cur = mCurrentElement;
			mCurrentElement = new ParagraphElement(mBook);
			mCurrentElement.mElementCursor.mRealFileStart = cur.mElementCursor.mRealFileLast+1;
			mCurrentElement.index = cur.index + 1;
			mCurrentElement.fill();
			return cur;
		}
	}

	public MarkupElement.Iterator iterator(int elementIndex, int realfilepos) {
		// need to judge which type of element
		// default text element now
		MarkupElement element = new ParagraphElement(mBook);
		element.mElementCursor.setRealFileStart(realfilepos);
		element.index = elementIndex;
		element.fill();
		IteratorIml iter = new IteratorIml();
		iter.mCurrentElement = element;
		return iter;
	}
}

package com.reader.book.manager;

public class BookPosition implements Comparable<BookPosition> {
	public BookPosition(int index, int bookoffset, int offset) {
		mElementIndex = index;
		mOffset = offset;
		mRealBookPos = bookoffset;
	}

	public BookPosition(BookPosition pos) {
		mElementIndex = pos.mElementIndex;
		mOffset = pos.mOffset;
		mRealBookPos = pos.mRealBookPos;
	}

	public int mElementIndex;
	public int mOffset;
	public int mRealBookPos;

	@Override
	public int compareTo(BookPosition another) {
		return (mElementIndex == another.mElementIndex
				&& mOffset == another.mOffset && mRealBookPos == another.mRealBookPos) ? 0
				: -1;
	}

	@Override
	public String toString() {
		return "bookposition:" + "index:" + mElementIndex + "realpos:"
				+ mRealBookPos + "mOffset:" + mOffset;
	}

}

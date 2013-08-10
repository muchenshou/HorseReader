package com.reader.book.model;

public class Cursor {
	public int mRealFileStart = 0;
	int mRealFileLast = 0;
	int index;
	public int len;
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getRealFileStart() {
		return mRealFileStart;
	}

	public void setRealFileStart(int mRealFileStart) {
		this.mRealFileStart = mRealFileStart;
	}

	public int getRealFileLast() {
		return mRealFileLast;
	}

	public void setRealFileLast(int mRealFileLast) {
		this.mRealFileLast = mRealFileLast;
	}

	public Cursor(Cursor c) {
		this.mRealFileStart = c.mRealFileStart;
		this.mRealFileLast = c.mRealFileLast;
	}

	public Cursor() {
		this.mRealFileStart = 0;
		this.mRealFileLast = -1;
	}

	public int getLength() {
		return mRealFileLast - mRealFileStart + 1;
	}
}
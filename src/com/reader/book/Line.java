package com.reader.book;

public class Line {
	public int mStart = 0;
	public int mLength = 0;// the length of the bytes
	public StringBuffer strLine = new StringBuffer();

	public int getEnd() {
		return mStart + mLength - 1;
	}
}
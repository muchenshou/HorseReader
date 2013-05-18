package com.reader.book.model;

class Cursor {
	public Cursor(Cursor c) {
		mPosition = c.mPosition;
	}

	public Cursor() {
		mPosition = -1;
	}

	public void setPos(int pos) {
		mPosition = pos;
	}
	public int getPos() {
		return mPosition;
	}
	int mPosition;
}
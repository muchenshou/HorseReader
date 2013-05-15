package com.reader.book.model;

class Cursor {
	public Cursor(Cursor c) {
		mPosition = c.mPosition;
	}

	int mPosition;
}
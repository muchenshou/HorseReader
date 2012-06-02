package com.Reader.Book.BookView;

import com.Reader.Book.Manager.BookReading;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BookProgressObj extends DrawableObj{

	public BookReading mBookReading;
	public int mSize;
	public BookProgressObj(BookReading bookreading,int size) {
		this.mBookReading = bookreading;
		this.mSize = size;
	}

	@Override
	public void Draw(Canvas canvas, Paint paint) {
		canvas.drawText(Float.toString(this.mBookReading.getCurPosition()/mSize), this.mPosX, this.mPosY, paint);
	}

}

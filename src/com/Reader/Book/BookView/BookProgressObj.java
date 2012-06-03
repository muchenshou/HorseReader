package com.Reader.Book.BookView;

import com.Reader.Book.Manager.BookReading;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class BookProgressObj extends DrawableObj{

	public BookReading mBookReading;
	public int mSize;
	public BookProgressObj(BookReading bookreading,int size) {
		this.mBookReading = bookreading;
		this.mSize = size;
	}

	@Override
	public void Draw(Canvas canvas, Paint paint) {
		Log.i("[bookprogress]", ""+this.mBookReading.getCurPosition()+"size:"+mSize);
		canvas.drawText(Float.toString((float)this.mBookReading.getCurPosition()/(float)mSize*100)+"%", this.mPosX, this.mPosY, paint);
	}

}

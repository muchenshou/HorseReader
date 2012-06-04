package com.Reader.Book.BookView;

import java.text.DecimalFormat;

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
		//Log.i("[bookprogress]", ""+this.mBookReading.getCurPosition()+"size:"+mSize);
		float fPercent = (float)this.mBookReading.getCurPosition()/(float)mSize;
		DecimalFormat df = new DecimalFormat("#0.0");
		String strPercent = df.format(fPercent * 100) + "%";
		//int nPercentWidth = (int) paint.measureText("999.9%") + 1;
		canvas.drawText(strPercent, this.mPosX, this.mPosY, paint);
	}

}

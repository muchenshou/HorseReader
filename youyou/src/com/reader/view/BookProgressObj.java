/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.view;

import java.text.DecimalFormat;

import com.reader.book.manager.PageProvider;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BookProgressObj extends DrawableObj {

	public PageProvider mBookReading;
	public int mSize;

	public BookProgressObj(PageProvider bookreading, int size) {
		this.mBookReading = bookreading;
		this.mSize = size;
	}

	@Override
	public void Draw(Canvas canvas, Paint paint) {
		// Log.i("[bookprogress]",
		// ""+this.mBookReading.getCurPosition()+"size:"+mSize);
		float fPercent = 0.0f;//(float) this.mBookReading.getCurPosition()
				/// (float) mSize;
		DecimalFormat df = new DecimalFormat("#0.0");
		String strPercent = df.format(fPercent * 100) + "%";
		// int nPercentWidth = (int) paint.measureText("999.9%") + 1;
		canvas.drawText(strPercent, this.mPosX, this.mPosY, paint);
	}

}

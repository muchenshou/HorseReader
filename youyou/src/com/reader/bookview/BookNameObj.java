/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.bookview;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BookNameObj extends DrawableObj {
	private String mBookName;

	public void setBookName(String name) {
		this.mBookName = name;
	}

	public float getNameMeasure(Paint paint) {
		return paint.measureText(mBookName);
	}

	@Override
	public void Draw(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		canvas.drawText(mBookName, this.mPosX, this.mPosY, paint);
	}

}

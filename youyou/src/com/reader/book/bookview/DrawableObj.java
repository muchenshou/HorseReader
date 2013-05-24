/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.bookview;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class DrawableObj {
	int mObjWidth = 0;
	int mObjHeight = 0;
	int mPosX = 0;
	int mPosY = 0;

	void setPosition(int x, int y) {
		this.mPosX = x;
		this.mPosY = y;
	}

	void setWH(int w, int h) {
		this.mObjWidth = w;
		this.mObjHeight = h;
	}

	public abstract void Draw(Canvas canvas, Paint paint);
}

package com.reader.book.bookview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public abstract class BookViewAnimation {
	//public void startAnimation();
	public abstract void setCurBitmap(Bitmap bitmap);
	public abstract void setNextBitmap(Bitmap bitmap);
	public abstract boolean DragToRight();
	public abstract void setBookView(BookView bookview);
	public abstract boolean onTouch(View v, MotionEvent event);
	public abstract void onSizeChange(int w, int h, int oldw, int oldh);
	public abstract void onDraw(Canvas canvas);
	public abstract void update();
	public static final int STATE_TOUCH = 0;
	public static final int STATE_ANIMATION = 1;
	public static final int NONE = 2;
	private int mState = NONE;
	public int state() {
		return mState;
	}
}

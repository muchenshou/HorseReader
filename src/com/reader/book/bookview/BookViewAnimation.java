package com.reader.book.bookview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public abstract class BookViewAnimation {
	// public void startAnimation();
	public abstract void setFrontBitmap(Bitmap bitmap);

	public abstract void setBackBitmap(Bitmap bitmap);

	public abstract boolean DragToRight();

	public abstract void setBookView(BookView bookview);

	public abstract boolean onTouch(View v, MotionEvent event);

	public abstract void onSizeChange(int w, int h, int oldw, int oldh);

	public abstract void onDraw(Canvas canvas);

	public abstract void update();

	public static final int STATE_TOUCH_START = 0;
	public static final int STATE_TOUCHING = 1;
	public static final int STATE_ANIMATION = 2;
	public static final int STATE_ANIMATION_END = 3;
	public static final int NONE = 4;
	protected int mState = NONE;

	public int state() {
		return mState;
	}

	public void setState(int s) {
		mState = s;
	}
}

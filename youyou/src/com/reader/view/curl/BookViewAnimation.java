package com.reader.view.curl;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public abstract class BookViewAnimation {
	// public void startAnimation();
	public interface BitmapSetup {
		public abstract Bitmap frontBitmap();
		public abstract Bitmap backBitmap();
		public void requestFresh();
	}
	protected BitmapSetup mBitmapSetup;
	
	public BookViewAnimation(BitmapSetup setup){
		mBitmapSetup = setup;
	}
	public abstract boolean DragToRight();

	public abstract void setBookView(View bookview);

	public abstract boolean onTouch(View v, MotionEvent event);

	public abstract void onSizeChange(int w, int h, int oldw, int oldh);

	public abstract void onDrawFrame(GL10 gl);

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

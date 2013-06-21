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
		public void turnToPre();
		public void turnToNext();
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

	// Curl state. We are flipping none, left or right page.
	public static final int CURL_LEFT = 1;
	public static final int CURL_NONE = 0;
	public static final int CURL_RIGHT = 2;

	// Constants for mAnimationTargetEvent.
	public static final int SET_CURL_TO_LEFT = 1;
	public static final int SET_CURL_TO_RIGHT = 2;
	protected boolean mAnimate = false;
	protected int mCurlState = CURL_NONE;

	public int state() {
		return mCurlState;
	}

	public void setState(int s) {
		mCurlState = s;
	}
}

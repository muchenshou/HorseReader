package com.reader.book.bookview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class NoTurnAnimation extends BookViewAnimation {
	private Bitmap mCurBitmap;
	private Bitmap mNextBitmap;
	private BookView mBookView;
	int mWidth;
	boolean isTurnToPre;
	Context mContext;
	GestureDetector mMyDetector;
	class MyGestureDetector extends SimpleOnGestureListener {

		private int verticalMinDistance = 20;
		private int minVelocity = 0;

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX() > verticalMinDistance
					&& Math.abs(velocityX) > minVelocity) {
				// Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();
			} else if (e2.getX() - e1.getX() > verticalMinDistance
					&& Math.abs(velocityX) > minVelocity) {
				// Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();
			}
			mState = STATE_ANIMATION_END;
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			if (e.getX() < mWidth / 2)
				isTurnToPre = true;
			else
				isTurnToPre = false;
			mState = STATE_ANIMATION_END;
		}

	}
	public NoTurnAnimation(Context context) {
		mMyDetector = new GestureDetector(new MyGestureDetector());
	}
	@Override
	public void setCurBitmap(Bitmap bitmap) {
		mCurBitmap = bitmap;
	}

	@Override
	public void setNextBitmap(Bitmap bitmap) {
		mNextBitmap = bitmap;
	}

	@Override
	public boolean DragToRight() {
		return isTurnToPre;
	}

	@Override
	public void setBookView(BookView bookview) {
		mBookView = bookview;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)
			mState = STATE_TOUCH_START;
		if (event.getAction() == MotionEvent.ACTION_MOVE)
			mState = STATE_TOUCHING;
		mMyDetector.onTouchEvent(event);
		return false;
	}

	@Override
	public void onSizeChange(int w, int h, int oldw, int oldh) {
		mWidth = w;
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawBitmap(mCurBitmap, 0, 0, null);
	}

	@Override
	public void update() {

	}

}

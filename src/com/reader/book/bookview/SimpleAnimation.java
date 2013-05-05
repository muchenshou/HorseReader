package com.reader.book.bookview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class SimpleAnimation extends BookViewAnimation {

	private Bitmap mFrontBitmap;
	private Bitmap mBackBitmap;
	GradientDrawable mShadowR;
	GradientDrawable mShadowL;
	int mWidth;
	int[] mFrontShadowColors = new int[] { 0x80111111, 0x111111 };
	Context mContext;
	View mAnimationView;
	float mTouch = 0f;
	boolean isTurnToPre = false;

	enum dir {
		GO, BACK
	};

	dir mPagedir = dir.GO;

	public SimpleAnimation(Context context) {
		mContext = context;

		mShadowR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
		mShadowR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		mShadowL = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
		mShadowL.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		mMyDetector = new GestureDetector(new MyGestureDetector());
	}

	@Override
	public void setFrontBitmap(Bitmap bitmap) {
		mFrontBitmap = bitmap;
	}

	@Override
	public void setBackBitmap(Bitmap bitmap) {
		mBackBitmap = bitmap;
	}

	@Override
	public boolean DragToRight() {
		return isTurnToPre;
	}

	@Override
	public void setBookView(BookView bookview) {
		mAnimationView = bookview;
	}

	float clickDown;
	float clickUp;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mMyDetector.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mState = STATE_TOUCH_START;
			mTouch = event.getX();
			clickDown = event.getX();
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			mState = STATE_TOUCHING;
			mTouch = event.getX();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			clickUp = event.getX();
			if (mIsGesture) {
				mTouch = mPagedir == dir.GO ? mWidth : 0;
				mIsGesture = false;
				mStartX = (int) mTouch;
				mEndX = mWidth - mTouch;

			} else {
				mPagedir = (clickUp - clickDown > 0) ? dir.GO : dir.BACK;
				mStartX = (int) mTouch;
				mEndX = mPagedir == dir.GO ? mWidth : 0;
			}
			isTurnToPre = mPagedir == dir.GO ? false : true;
			if (isTurnToPre) {
				Bitmap m;
				m = mFrontBitmap;
				mFrontBitmap = mBackBitmap;
				mBackBitmap = m;
			}
			startAnimation(DELAY_TURN_RIGHT);
		}
		return true;
	}

	@Override
	public void onSizeChange(int w, int h, int oldw, int oldh) {
		mWidth = w;
	}

	@Override
	public void onDraw(Canvas canvas) {
		DrawFront(canvas);
		DrawBack(canvas);
		DrawShadow(canvas);
		animation();
	}

	private void DrawShadow(Canvas canvas) {
		mShadowL.setBounds((int) mTouch, 0, (int) mTouch + 20,
				mAnimationView.getHeight());
		mShadowL.draw(canvas);
	}

	private void DrawFront(Canvas canvas) {
		// Path path = new Path();
		// path.reset();
		// path.addRect(0, 0, 500, mAnimationView.getHeight(),
		// Path.Direction.CCW);

		canvas.save();
		canvas.translate(-(mAnimationView.getWidth() - mTouch), 0);
		// canvas.clipPath(path);
		canvas.drawBitmap(mFrontBitmap, 0, 0, new Paint());

		canvas.restore();
	}

	private void DrawBack(Canvas canvas) {
		Path path = new Path();
		path.reset();
		path.addRect(mTouch, 0, mAnimationView.getWidth(),
				mAnimationView.getHeight(), Path.Direction.CCW);

		canvas.save();
		canvas.clipPath(path);
		canvas.drawBitmap(mBackBitmap, 0, 0, new Paint());

		canvas.restore();
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	private static int DELAY_TURN_RIGHT = 400;
	long mEnd;
	long mStart;
	float mStartX;
	float mEndX;
	float mX;
	float mY;

	private void startAnimation(int delayMillis) {
		mStart = System.currentTimeMillis();
		mEnd = mStart + delayMillis;
		mX = mEndX - mStartX;
		this.mState = STATE_ANIMATION;
		mAnimationView.postInvalidate();
	}

	private void animation() {
		if (mState != STATE_ANIMATION)
			return;
		long now = System.currentTimeMillis();
		if (now < mEnd) {
			mTouch = mStartX + mX * (now - mStart) / DELAY_TURN_RIGHT;
			mAnimationView.postInvalidate();
		} else {
			mState = STATE_ANIMATION_END;
			mTouch = mWidth;
			mAnimationView.postInvalidate();
		}
	}

	GestureDetector mMyDetector;
	boolean mIsGesture = false;

	class MyGestureDetector extends SimpleOnGestureListener {

		private int verticalMinDistance = 150;
		private int minVelocity = 0;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.i("hello", "onFling:"+e1.getX()+"Motion:"+e2.getX());
			if (e1.getX() - e2.getX() > verticalMinDistance
					&& Math.abs(velocityX) > minVelocity) {
				// Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();
				mIsGesture = true;
				mPagedir = dir.GO;
			} else if (e2.getX() - e1.getX() > verticalMinDistance
					&& Math.abs(velocityX) > minVelocity) {
				// Toast.makeText(this, "向右手势", Toast.LENGTH_SHORT).show();
				mIsGesture = true;
				mPagedir = dir.BACK;
			}
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			Log.i("hello", "onDown:"+e.getX());
			return super.onDown(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			Log.i("hello", "onDown:"+e1.getX()+"Motion:"+e2.getX());
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.i("hello", "onDown:"+e.getX());
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onSingleTapUp(e);
		}

//		@Override
//		public void onShowPress(MotionEvent e) {
//			if (e.getX() < mWidth / 2)
//				mPagedir = dir.BACK;
//			else
//				mPagedir = dir.GO;
//		}

	}
}

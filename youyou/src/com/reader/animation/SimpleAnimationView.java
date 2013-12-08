package com.reader.animation;

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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

public class SimpleAnimationView extends AnimationView {
	GradientDrawable mShadowR;
	GradientDrawable mShadowL;
	IAnimation.DIR _dir = IAnimation.DIR.NEXT;
	int mWidth;
	int[] mFrontShadowColors = new int[] { 0x80111111, 0x111111 };
	int[] mShadowRColors = new int[] { 0xFF000000, 0x00000000 };
	// int[] mShadowRColors = new int[] { 0x111111, 0x80111111 };
	Context mContext;
	Paint _paint = new Paint();
	float mBoundLine = 0f;
	long mEnd; // time
	long mStart; // time
	float mStartX; // the position of start
	float mEndX; // the position of end
	float mX; // x the position current
	float mY; // y
	int animationtime = 300;
	private static final Interpolator sInterpolator = new AccelerateInterpolator();
	protected boolean mAnimate = false;
	private GestureDetector _detector;

	public SimpleAnimationView(Context context, IAnimation ani) {
		super(context, ani);
		mShadowR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mShadowRColors);
		mShadowR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		mShadowL = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
		mShadowL.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		_detector = new GestureDetector(new GestureListener());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mAnimate) {
			DrawBack(canvas);
			DrawFront(canvas);
			animation();
		} else {
			if (_bitmaps[1] != null)
				canvas.drawBitmap(_bitmaps[1], 0, 0, _paint);
		}
	}

	private boolean isTurnToPre() {
		return _dir == IAnimation.DIR.PRE;
	}

	private void DrawFront(Canvas canvas) {
		int shadowW = 50;
		canvas.save();
		canvas.translate(mBoundLine - getWidth(), 0);
		Bitmap b = isTurnToPre() ? _bitmaps[0] : _bitmaps[1];
		if (b != null) {
			mShadowR.setBounds((int) getWidth(), 0, (int) getWidth() + shadowW,
					getHeight());
			mShadowR.draw(canvas);
			canvas.drawBitmap(b, 0, 0, _paint);
		}
		canvas.restore();
	}

	private void DrawBack(Canvas canvas) {
		Path path = new Path();
		path.reset();
		path.addRect(mBoundLine, 0, getHeight(), getHeight(),
				Path.Direction.CCW);

		canvas.save();
		canvas.clipPath(path);
		Bitmap b = isTurnToPre() ? _bitmaps[1] : _bitmaps[2];
		if (b != null) {
			canvas.drawBitmap(b, 0, 0, _paint);
		}

		canvas.restore();
	}

	private void animation() {
		if (!mAnimate)
			return;
		long now = System.currentTimeMillis();
		if (now < mEnd) {
			mBoundLine = mStartX
					+ mX
					* sInterpolator.getInterpolation((float) (now - mStart)
							/ (float) animationtime);
			postInvalidate();
		} else {
			mAnimate = false;
			mBoundLine = mWidth;
			postAnimationEnd();
			postInvalidate();
		}
	}

	private void postAnimationStart() {
		if (isTurnToPre()) {
			if (_bitmaps[0] == null) {
				return;
			}
		} else {
			if (_bitmaps[2] == null) {
				return;
			}
		}
		mAnimate = true;
		mStart = System.currentTimeMillis();
		mEnd = mStart + animationtime;
		invalidate();
		if (isTurnToPre()) {
			mStartX = 0 + 10;
			mEndX = getWidth();
		} else {
			mStartX = getWidth() - 10;
			mEndX = 0;
		}
		mX = mEndX - mStartX;
		mBoundLine = mStartX;
		_animation.startAnimation(0);
	}

	private void postAnimationEnd() {
		_animation.endAnimation(_dir);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return _detector.onTouchEvent(event);
	}

	class GestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onDoubleTap");
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub

			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			super.onLongPress(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onSingleTapUp" + e.getX() + "y:" + e.getY());
			if (e.getX() > getWidth() / 2) {
				_dir = IAnimation.DIR.NEXT;
			} else {
				_dir = IAnimation.DIR.PRE;
			}
			postAnimationStart();
			return super.onSingleTapUp(e);
		}

	}
}

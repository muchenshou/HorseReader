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
	int[] mShadowRColors = new int[] { 0x111111, 0x80111111 };
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
			DrawFront(canvas);
//			DrawBack(canvas);
//			DrawShadow(canvas);
			animation();
		} else {
			canvas.drawBitmap(_bitmaps[1], 0, 0, _paint);
		}
	}

	private boolean isTurnToPre() {
		return _dir == IAnimation.DIR.PRE;
	}

	private void DrawShadow(Canvas canvas) {
		if (isTurnToPre()) {
			mShadowR.setBounds((int) mBoundLine + 20, 0, (int) mBoundLine,
					getHeight());
			mShadowR.draw(canvas);
		} else {
			mShadowL.setBounds((int) mBoundLine, 0, (int) mBoundLine + 20,
					getHeight());
			mShadowL.draw(canvas);
		}
	}

	private void DrawFront(Canvas canvas) {
		canvas.save();
		canvas.translate(isTurnToPre() ? mBoundLine - getWidth() : mBoundLine,
				0);
		Bitmap b = isTurnToPre() ? _bitmaps[0] : _bitmaps[2];
		if (b != null) {
			canvas.drawBitmap(b, 0, 0, _paint);
		}
		canvas.restore();
	}

	private void DrawBack(Canvas canvas) {
		Path path = new Path();
		path.reset();
		if (isTurnToPre())
			path.addRect(mBoundLine, 0, getHeight(), getHeight(),
					Path.Direction.CCW);
		else
			path.addRect(0, 0, mBoundLine, getHeight(), Path.Direction.CCW);

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
		mAnimate = true;
		mStart = System.currentTimeMillis();
		mEnd = mStart + animationtime;
		if (isTurnToPre()) {
			mStartX = 0;
			mEndX = getWidth();
		} else {
			mStartX = getWidth();
			mEndX = 0;
		}
		mX = mEndX - mStartX;
		postInvalidate();
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

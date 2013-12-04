package com.reader.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

public class SimpleAnimationView extends AnimationView {
	GradientDrawable mShadowR;
	GradientDrawable mShadowL;
	int mWidth;
	int[] mFrontShadowColors = new int[] { 0x80111111, 0x111111 };
	int[] mShadowRColors = new int[] { 0x111111, 0x80111111 };
	Context mContext;
	float mBoundLine = 0f;
	boolean isTurnToPre = false;
	long mEnd;
	long mStart;
	float mStartX;
	float mEndX;
	float mX;
	float mY;
	int animationtime;
	private static final Interpolator sInterpolator = new AccelerateInterpolator();
	protected boolean mAnimate = false;

	public SimpleAnimationView(Context context, IAnimation ani) {
		super(context, ani);
		mShadowR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mShadowRColors);
		mShadowR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		mShadowL = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
		mShadowL.setGradientType(GradientDrawable.LINEAR_GRADIENT);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		DrawFront(canvas);
		DrawBack(canvas);
		DrawShadow(canvas);
		animation();
	}

	private void DrawShadow(Canvas canvas) {
		if (isTurnToPre) {
			mShadowR.setBounds((int) mBoundLine - 20, 0, (int) mBoundLine,
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
		canvas.translate(isTurnToPre ? mBoundLine : -(getWidth() - mBoundLine),
				0);
		if (_bitmaps[2] != null)
			canvas.drawBitmap(_bitmaps[2], 0, 0, new Paint());
		canvas.restore();
	}

	private void DrawBack(Canvas canvas) {
		Path path = new Path();
		path.reset();
		if (isTurnToPre)
			path.addRect(0, 0, mBoundLine, getHeight(), Path.Direction.CCW);
		else
			path.addRect(mBoundLine, 0, getWidth(), getHeight(),
					Path.Direction.CCW);

		canvas.save();
		canvas.clipPath(path);
		if (_bitmaps[2] != null)
			canvas.drawBitmap(_bitmaps[2], 0, 0, new Paint());

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
			postInvalidate();
		}
	}
}

package com.reader.bookview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

public class SimpleAnimation extends BookViewAnimation {

	private Bitmap mFrontBitmap;
	private Bitmap mBackBitmap;
	GradientDrawable mShadowR;
	GradientDrawable mShadowL;
	int mWidth;
	int[] mFrontShadowColors = new int[] { 0x80111111, 0x111111 };
	int[] mShadowRColors = new int[] { 0x111111, 0x80111111 };
	Context mContext;
	View mAnimationView;
	float mBoundLine = 0f;
	boolean isTurnToPre = false;

	enum dir {
		GO, BACK
	};

	dir mPagedir = dir.GO;

	public SimpleAnimation(Context context) {
		mContext = context;

		mShadowR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mShadowRColors);
		mShadowR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		mShadowL = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
		mShadowL.setGradientType(GradientDrawable.LINEAR_GRADIENT);
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
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mState = STATE_TOUCH_START;
			mBoundLine = mWidth;
			clickDown = event.getX();
			isTurnToPre = false;
			setBackBitmap(BookScreenDisplay.Instance
					.tranlateBackBitmap(BookScreenDisplay.NEXT));
		}
		boolean pre = false;
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			mState = STATE_TOUCHING;
			pre = clickDown < event.getX() ? true : false;
			if (pre != isTurnToPre) {
				isTurnToPre = pre;
				// Draw current page and Set
				setFrontBitmap(BookScreenDisplay.Instance.tranlateFrontBitmap());

				// Draw next or pre page and Set it
				if (DragToRight()) {
					setBackBitmap(BookScreenDisplay.Instance
							.tranlateBackBitmap(BookScreenDisplay.PRE));
				} else {
					setBackBitmap(BookScreenDisplay.Instance
							.tranlateBackBitmap(BookScreenDisplay.NEXT));
				}
			}
			if (isTurnToPre) {
				mBoundLine = event.getX() - clickDown;
			} else {
				mBoundLine = mWidth + event.getX() - clickDown;
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			clickUp = event.getX();
			if (isTurnToPre) {
				mBoundLine = clickUp - clickDown;
				mEndX = mWidth;
			} else {
				mBoundLine = mWidth + clickUp - clickDown;
				mEndX = 0;
			}
			mStartX = (int) mBoundLine;
			mPagedir = (clickUp - clickDown <= 0) ? dir.GO : dir.BACK;
			isTurnToPre = mPagedir == dir.GO ? false : true;

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
		if (isTurnToPre) {
			mShadowR.setBounds((int) mBoundLine - 20, 0, (int) mBoundLine,
					mAnimationView.getHeight());
			mShadowR.draw(canvas);
		} else {
			mShadowL.setBounds((int) mBoundLine, 0, (int) mBoundLine + 20,
					mAnimationView.getHeight());
			mShadowL.draw(canvas);
		}
	}

	private void DrawFront(Canvas canvas) {
		canvas.save();
		canvas.translate(isTurnToPre ? mBoundLine
				: -(mAnimationView.getWidth() - mBoundLine), 0);
		canvas.drawBitmap(mFrontBitmap, 0, 0, new Paint());
		canvas.restore();
	}

	private void DrawBack(Canvas canvas) {
		Path path = new Path();
		path.reset();
		if (isTurnToPre)
			path.addRect(0, 0, mBoundLine, mAnimationView.getHeight(),
					Path.Direction.CCW);
		else
			path.addRect(mBoundLine, 0, mAnimationView.getWidth(),
					mAnimationView.getHeight(), Path.Direction.CCW);

		canvas.save();
		canvas.clipPath(path);
		canvas.drawBitmap(mBackBitmap, 0, 0, new Paint());

		canvas.restore();
	}

	@Override
	public void update() {

	}

	private static int DELAY_TURN_RIGHT = 230;
	long mEnd;
	long mStart;
	float mStartX;
	float mEndX;
	float mX;
	float mY;
	int animationtime;

	private void startAnimation(int delayMillis) {

		mStart = System.currentTimeMillis();
		mX = mEndX - mStartX;
		animationtime = delayMillis;// (int)((float)delayMillis*((float)mX/(float)mWidth));
		mEnd = mStart + animationtime;
		this.mState = STATE_ANIMATION;
		mAnimationView.postInvalidate();
	}

	private static final Interpolator sInterpolator = new AccelerateInterpolator();

	private void animation() {
		if (mState != STATE_ANIMATION)
			return;
		long now = System.currentTimeMillis();
		if (now < mEnd) {
			mBoundLine = mStartX
					+ mX
					* sInterpolator.getInterpolation((float) (now - mStart)
							/ (float) animationtime);
			mAnimationView.postInvalidate();
		} else {
			mState = STATE_ANIMATION_END;
			mBoundLine = mWidth;
			mAnimationView.postInvalidate();
		}
	}
}

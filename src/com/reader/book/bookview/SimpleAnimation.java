package com.reader.book.bookview;

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
	}

	@Override
	public void setFrontBitmap(Bitmap bitmap) {
		if (isTurnToPre) {
			mBackBitmap = bitmap;
			return;
		}
		mFrontBitmap = bitmap;
	}

	@Override
	public void setBackBitmap(Bitmap bitmap) {
		if (isTurnToPre) {
			mFrontBitmap = bitmap;
			return;
		}
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
			mTouch = mWidth;
			clickDown = event.getX();
			isTurnToPre = clickDown < mWidth / 2 ? true : false;
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			mState = STATE_TOUCHING;
			if (isTurnToPre){
				mTouch = event.getX() - clickDown;
			}else {
				mTouch = mWidth + event.getX() - clickDown;
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			clickUp = event.getX();
			if (isTurnToPre){
				mTouch = clickUp - clickDown;
				mEndX =  mWidth;
			}else {
				mTouch = mWidth + clickUp - clickDown;
				mEndX =  0;
			}
			mStartX = (int) mTouch;
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
		mShadowL.setBounds((int) mTouch, 0, (int) mTouch + 20,
				mAnimationView.getHeight());
		mShadowL.draw(canvas);
	}

	private void DrawFront(Canvas canvas) {
		canvas.save();
		canvas.translate(-(mAnimationView.getWidth() - mTouch), 0);
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

	}

	private static int DELAY_TURN_RIGHT = 400;
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
			mTouch = mStartX
					+ mX
					* sInterpolator.getInterpolation((float) (now - mStart)
							/ (float) animationtime);
			mAnimationView.postInvalidate();
		} else {
			mState = STATE_ANIMATION_END;
			mTouch = mWidth;
			mAnimationView.postInvalidate();
		}
	}
}

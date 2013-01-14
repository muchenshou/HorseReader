package com.reader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;

public class AdobeView extends ViewGroup{

	private static final String TAG = "AdobeView";
	private static final boolean DEBUG = true;
	private boolean mHide = true;

	public AdobeView(Context context) {
		super(context);
	}

	public AdobeView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (DEBUG) {
			Log.d(TAG, "widthMeasureSpec = " + widthMeasureSpec
					+ " heightMeasureSpec" + heightMeasureSpec);
			Log.i(TAG, "getdefault:" + getDefaultSize(66, widthMeasureSpec)
					+ "height:" + getDefaultSize(66, heightMeasureSpec));
			Log.i(TAG, "resolvesize" + resolveSize(0, widthMeasureSpec)
					+ "height:" + resolveSize(0, heightMeasureSpec));
		}
		View child = this.getChildAt(0);

		int widthSpec = MeasureSpec.makeMeasureSpec(
				getDefaultSize(0, widthMeasureSpec), MeasureSpec.EXACTLY);
		int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST);
		if (DEBUG) {
			Log.i(TAG, "make measure:" + widthSpec + "height:" + heightSpec);
		}

		child.measure(widthSpec, 0);
		child = this.getChildAt(1);

		widthSpec = MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY);
		heightSpec = MeasureSpec.makeMeasureSpec(
				getDefaultSize(0, heightMeasureSpec), MeasureSpec.EXACTLY);

		child.measure(widthSpec, heightSpec);
		child = this.getChildAt(2);
		widthSpec = MeasureSpec.makeMeasureSpec(
				getDefaultSize(0, widthMeasureSpec), MeasureSpec.EXACTLY);
		heightSpec = MeasureSpec.makeMeasureSpec(
				getDefaultSize(0, heightMeasureSpec) - 40, MeasureSpec.EXACTLY);
		child.measure(widthSpec, heightSpec);
		setMeasuredDimension(getChildAt(0).getMeasuredWidth(), getChildAt(1)
				.getMeasuredHeight());
	}

	@Override
	protected void onLayout(boolean arg0, int l, int t, int r, int b) {
		if (DEBUG) {
			Log.d(TAG, String.format("%d\t%d\t%d\t%d\t", l, t, r, b));
		}
		View child = this.getChildAt(0);
		int childHeight;
		childHeight = child.getMeasuredHeight();
		this.getChildAt(0).layout(l, t, r, t + childHeight);
		if (!mHide) {

			child = this.getChildAt(1);
			this.getChildAt(1).layout(l, t + childHeight, r - 3 * (r - l) / 5,
					b);
			if (DEBUG) {
				Log.i(TAG, "child measureheight:" + child.getMeasuredHeight());
				Log.i(TAG, "child getHeight:" + child.getHeight());
			}

			this.getChildAt(2).layout(r - 3 * (r - l) / 5, t + childHeight,
					r + 3 * (r - l) / 5, b);
		} else {
			this.getChildAt(1).layout(l, t + childHeight, r - 3 * (r - l) / 5,
					b);
			this.getChildAt(2).layout(l, t + childHeight, r, b);
		}
	}

	public void switchView() {
		mHide = !mHide;
		Log.i(TAG, mHide ? "true" : "false");
		this.requestLayout();
		View child, child2;
		TranslateAnimation animation1, animation2;
		if (!mHide) {
			child = this.getChildAt(1);
			animation1 = new TranslateAnimation(
					(0.0f - (float) child.getWidth()), 0.0f, 0.0f, 0.0f);
			animation1.setDuration(500);
			child2 = this.getChildAt(2);

			animation2 = new TranslateAnimation(
					(0.0f - (float) child.getWidth()), 0.0f, 0.0f, 0.0f);
			animation2.setDuration(500);
			// child.startAnimation(animation1);
			child2.startAnimation(animation2);
		} else {
			child = this.getChildAt(1);
			child2 = this.getChildAt(2);
			animation2 = new TranslateAnimation(
					0.0f + (float) child.getWidth(), 0.0f, 0.0f, 0.0f);
			animation2.setDuration(500);
			child2.startAnimation(animation2);
		}

		// this.layout(mLeft, mTop, mRight, mBottom);
	}
}

package com.reader.app;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.reader.animation.AnimationView;
import com.reader.animation.IAnimation;
import com.reader.animation.SimpleAnimationView;

public class AllUi extends FrameLayout implements IAnimation {
	private View _MainUiView;
	private View _NetUiView;
	private AnimationView _animationView;
	private FragmentActivity mfa;
	private MainUi _MainUi;
	GestureDetector mGesture = null;

	public AllUi(FragmentActivity fa) {
		super(fa);
		mfa = fa;
		_MainUi = new MainUi(mfa);
		_MainUiView = _MainUi.create(R.layout.mainui);
		_NetUiView = new NetUi(mfa).create();
		_NetUiView.setVisibility(View.GONE);

		addView(_NetUiView);
		addView(_MainUiView);
		_animationView = new SimpleAnimationView(mfa, this);
		mGesture = new GestureDetector(mfa, new GestureListener());
	}

	@Override
	public int startAnimation(DIR flags) {
		return 0;
	}

	@Override
	public int endAnimation(DIR flags) {
		return 0;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mGesture.onTouchEvent(ev);
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
			Log.i("TEST", "onDown");
			return super.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onFling:velocityX = " + velocityX + " velocityY"
					+ velocityY);
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onLongPress");
			super.onLongPress(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onScroll:distanceX = " + distanceX + " distanceY = "
					+ distanceY);
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onSingleTapUp");
			return super.onSingleTapUp(e);
		}

	}
}

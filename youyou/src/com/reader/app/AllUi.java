package com.reader.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.reader.util.BitmapUtil;

public class AllUi extends FrameLayout {
	private View _MainUiView;
	private View _NetUiView;
	private View _box;
	private FragmentActivity mfa;
	private MainUi _MainUi;

	GestureDetector mGesture = null;
	Bitmap _bitmap_main;
	Bitmap _bitmap_net;
	boolean _animating = false;

	class Box extends View {

		public Box(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onDraw(Canvas canvas) {
			Log.i("song", "box:draw " + _animating);
			if (_animating) {
				calcPos();
				drawAnim(canvas);
				calcAnimTime();

				postInvalidate();
			}
			super.onDraw(canvas);
		}

	}
	
	public AllUi(FragmentActivity fa) {
		super(fa);
		mfa = fa;
		_MainUi = new MainUi(mfa);
		_MainUiView = _MainUi.create(R.layout.mainui);
		_NetUiView = new NetUi(mfa).create();
		_box = new Box(mfa);
		addView(_box);
		addView(_NetUiView);
		addView(_MainUiView);
		mGesture = new GestureDetector(mfa, new GestureListener());
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (_animating) {
			mGesture.onTouchEvent(ev);
			return true;
		}
		return mGesture.onTouchEvent(ev);
	}

	long _startanim;

	void calcAnimTime() {
		if (System.currentTimeMillis() > _startanim + 3000) {
			_animating = false;
			_MainUiView.setVisibility(View.VISIBLE);
			_NetUiView.setVisibility(View.VISIBLE);

			postInvalidate();

		}
	}

	Paint _paint = new Paint();
	int rotateX = 0;
	float rotateY = 0;
	int rotateZ = 0;
	float translateX = 0;
	float skewX = 0.9f, skewY=0.9f;
	
	void calcPos() {
		float b = (float) (System.currentTimeMillis() - _startanim) / 3000.0f;
		translateX =  (b*this.getWidth());
		rotateY = - (b * pai);
	}
	final float pai = 90.0f;
	int centerX = 0;
	int centerY = 0;
	void drawAnim(Canvas canvas) {
		centerX = getWidth()/2;
		centerY = getHeight()/2;
		
		Camera camera = new Camera();
		camera.save();
		camera.rotateY(rotateY);
		camera.translate(0, 0, 80);
		Matrix matrix = new Matrix();
		camera.getMatrix(matrix);
		camera.restore();
		//设置翻转中心点   
        matrix.preTranslate(-this.centerX, -this.centerY);  
        matrix.postTranslate(this.centerX, this.centerY);  
        matrix.postTranslate(-translateX, 0);  
        
		Bitmap tmpBit = _bitmap_main;

		canvas.drawBitmap(tmpBit, matrix, _paint);
	}

	class GestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			Log.i("TEST", "onFling:velocityX = " + velocityX + " velocityY"
					+ velocityY);
			_bitmap_main = BitmapUtil.convertViewToBitmap(_MainUiView);
			_bitmap_net = BitmapUtil.convertViewToBitmap(_NetUiView);
			_animating = true;
			_startanim = System.currentTimeMillis();
			_MainUiView.setVisibility(View.INVISIBLE);
			_NetUiView.setVisibility(View.INVISIBLE);
			_box.invalidate();
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
			// Log.i("TEST", "action1:"+e1.getAction());
			// Log.i("TEST", "action2:"+e2.getAction());
			// Log.i("TEST", "onScroll:distanceX = " + distanceX +
			// " distanceY = "
			// + distanceY);
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

	}
}

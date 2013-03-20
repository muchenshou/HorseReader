package com.reader.book.bookview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class NoTurnAnimation extends BookViewAnimation{
	private Bitmap mCurBitmap;
	private Bitmap mNextBitmap;
	private BookView mBookView;
	class MyGestureDetector extends SimpleOnGestureListener{

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			super.onShowPress(e);
		}
		
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
		
		return false;
	}

	@Override
	public void setBookView(BookView bookview) {
		mBookView = bookview;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	public void onSizeChange(int w, int h, int oldw, int oldh) {
		
	}

	@Override
	public void onDraw(Canvas canvas) {
		
	}

	@Override
	public void update() {
		
	}

}

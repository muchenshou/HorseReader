package com.reader.book.bookview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public interface BookViewAnimation {
	//public void startAnimation();
	public void setCurBitmap(Bitmap bitmap);
	public void setNextBitmap(Bitmap bitmap);
	public boolean DragToRight();
	public void abortAnimation();
	public void setBookView(BookView bookview);
	public boolean onTouch(View v, MotionEvent event);
	public void onSizeChange(int w, int h, int oldw, int oldh);
	public void onDraw(Canvas canvas);
	public void update();
	public void computeScroll();
}

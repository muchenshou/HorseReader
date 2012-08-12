package com.Reader.Book.BookView;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public interface BookViewAnimation {
	//public void startAnimation();
	public void abortAnimation();
	public void setBookView(BookView bookview);
	public boolean AfterTouch(View v, MotionEvent event);
	public void AfterSizeChange(int w, int h, int oldw, int oldh);
	public void AfterDraw(Canvas canvas);
	public void update();
	public void computeScroll();
}

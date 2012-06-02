package com.Reader.Book.BookView;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BookNameObj extends DrawableObj{
	private String mBookName;
	public void setBookName(String name){
		this.mBookName = name;
	}
	@Override
	public void Draw(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		canvas.drawText(mBookName, this.mPosX, this.mPosY, paint);
	}

}

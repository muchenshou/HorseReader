/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.bookview;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Canvas;
import android.graphics.Paint;

public class TimeObj extends DrawableObj {

	@Override
	public void Draw(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		//
		SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
		Date date = new Date();
		canvas.drawText(fmt.format(date), this.mPosX, this.mPosY, paint);
	}

}

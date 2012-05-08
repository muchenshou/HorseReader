package com.Reader.Config;

import android.graphics.Color;
import android.graphics.Paint;

public class TextUtilConfig {
	public int mFontColor = 0;// 字体颜色
	public int mAlpha = 0;// Alpha值
	public int mTextSize = 0;// 字体大小
	public int mPadding = 5;
	private Paint mPaint = null;
	public TextUtilConfig(){
		this.mFontColor = Color.BLACK;
		this.mAlpha = 0;
		this.mTextSize = 15;
		this.mPadding = 2;
	}
	public Paint getPaint() {
		mPaint = new Paint();
		mPaint.setARGB(this.mAlpha, Color.red(this.mFontColor), Color
				.green(this.mFontColor), Color.blue(this.mFontColor));
		mPaint.setTextSize(this.mTextSize);
		mPaint.setColor(this.mFontColor);
		return mPaint;
	}
}

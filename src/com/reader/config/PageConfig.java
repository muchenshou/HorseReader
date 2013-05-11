/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.config;

import com.reader.util.PaintText;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;

public class PageConfig {
	public int mFontColor;// 字体颜色
	public int mAlpha;// Alpha值
	private int mTextSize;// 字体大小
	public int mPadding;

	private Paint mPaint = null;
	private Paint mOthersPaint = null;
	private Context mContext;

	public PageConfig(Context con) {
		mContext = con;
		getConfig();
	}

	public void getConfig() {
		this.mFontColor = Configs.getInt("fontcolor", 
				Color.BLACK);
		this.mAlpha = Configs.getInt("alpha", 0);
		this.mTextSize = Configs.getInt("textsize", 20);
		this.mPadding = Configs.getInt("padding", 0);
	}

	public void saveConfig() {
		Configs.putInt("fontcolor", this.mFontColor);
		Configs.putInt("alpha", this.mAlpha);
		Configs.putInt("textsize", this.mTextSize);
		Configs.putInt("padding", this.mPadding);
	}

	public void setTextSize(int size) {
		this.mTextSize = size;
		float textSize = PaintText.getRawSize(this.mContext,
				TypedValue.COMPLEX_UNIT_SP, size);
		this.mPaint.setTextSize(textSize);
	}

	public int getTextSize() {
		return mTextSize;
	}

	public Paint getPaint() {
		if (mPaint != null) {
			return mPaint;
		}
		mPaint = new Paint();
		mPaint.setARGB(this.mAlpha, Color.red(this.mFontColor),
				Color.green(this.mFontColor), Color.blue(this.mFontColor));
		mPaint.setTextSize(PaintText.getRawSize(this.mContext,
				TypedValue.COMPLEX_UNIT_SP, this.mTextSize));
		mPaint.setColor(this.mFontColor);
		mPaint.setAntiAlias(true);
		mPaint.setTypeface(Typeface.MONOSPACE);
		return mPaint;
	}

	public Paint getOthersPaint() {
		if (mOthersPaint != null) {
			return mOthersPaint;
		}
		mOthersPaint = new Paint();
		mOthersPaint.setARGB(this.mAlpha, Color.red(this.mFontColor),
				Color.green(this.mFontColor), Color.blue(this.mFontColor));
		mOthersPaint.setTextSize(15);
		mOthersPaint.setColor(this.mFontColor);
		mOthersPaint.setAntiAlias(true);
		mOthersPaint.setTypeface(Typeface.MONOSPACE);
		return mOthersPaint;
	}
}

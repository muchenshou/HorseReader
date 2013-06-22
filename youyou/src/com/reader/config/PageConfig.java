/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.config;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Typeface;
import android.util.TypedValue;

import com.reader.app.YouYouApplication;
import com.reader.util.PaintText;

public class PageConfig {
	// public void setTextSize(int size) {
	// this.mTextSize = size;
	// float textSize = PaintText.getRawSize(this.mContext,
	// TypedValue.COMPLEX_UNIT_SP, size);
	// this.mPaint.setTextSize(textSize);
	// }

	public static int getTextSize() {
		return Configs.getInt("textsize", 20);
	}

	private static Paint mPaint;
	private static FontMetrics fm;
	public static Paint pagePaintFromConfig(boolean update) {
		if (mPaint != null && !update) {
			return mPaint;
		}
		int mFontColor;// 字体颜色
		int mAlpha;// Alpha值
		int mTextSize;// 字体大小
		mFontColor = Configs.getInt("fontcolor", Color.BLACK);
		mAlpha = Configs.getInt("alpha", 0);
		mTextSize = Configs.getInt("textsize", 20);
		mPaint = new Paint();
		mPaint.setARGB(mAlpha, Color.red(mFontColor), Color.green(mFontColor),
				Color.blue(mFontColor));
		mPaint.setTextSize(PaintText.getRawSize(YouYouApplication.Instance,
				TypedValue.COMPLEX_UNIT_SP, mTextSize));
		mPaint.setColor(mFontColor);
		mPaint.setAntiAlias(true);
		mPaint.setTypeface(Typeface.MONOSPACE);
		fm = mPaint.getFontMetrics();//
		return mPaint;
	}

	private static Paint mOthersPaint;

	public static Paint getOthersPaint(boolean update) {
		if (mOthersPaint != null && !update) {
			return mOthersPaint;
		}
		int mFontColor;// 字体颜色
		int mAlpha;// Alpha值
		int mTextSize;// 字体大小
		mFontColor = Configs.getInt("bottom_fontcolor", Color.BLACK);
		mAlpha = Configs.getInt("bottom_alpha", 0);
		mTextSize = Configs.getInt("bottom_textsize", 20);
		mOthersPaint = new Paint();
		mOthersPaint.setARGB(mAlpha, Color.red(mFontColor),
				Color.green(mFontColor), Color.blue(mFontColor));
		mOthersPaint.setTextSize(mTextSize);
		mOthersPaint.setColor(mFontColor);
		mOthersPaint.setAntiAlias(true);
		mOthersPaint.setTypeface(Typeface.MONOSPACE);
		return mOthersPaint;
	}

	public static int getPadding() {
		return Configs.getInt("padding", 0);
	}

	public static int getTextHeight(Paint paint) {
		return (int) (Math.ceil(fm.descent - fm.top) + 1);
	}
	
	public static float getBaseLineText(){
		return fm.top;
	}
}

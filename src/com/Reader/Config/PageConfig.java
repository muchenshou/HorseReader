package com.Reader.Config;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;

public class PageConfig {
	public int mFontColor;// 字体颜色
	public int mAlpha;// Alpha值
	public int mTextSize;// 字体大小
	public int mPadding;
	
	private SharedPreferences mTextConfig;
	final private String mConfigFileName = "textconfig"; 
	private Paint mPaint = null;
	private Context mContext;
	public PageConfig(Context con){
		mContext = con;
		getConfig();
	}
	public void getConfig() {
		this.mTextConfig = this.mContext.getSharedPreferences(this.mConfigFileName, 0);
		this.mFontColor = this.mTextConfig.getInt("fontcolor", Color.BLACK);
		this.mAlpha = this.mTextConfig.getInt("alpha", 0);
		this.mTextSize = this.mTextConfig.getInt("textsize", 20);
		this.mPadding = this.mTextConfig.getInt("padding", 0);
	}
	public void saveConfig(){
		SharedPreferences.Editor editor = this.mTextConfig.edit();
		editor.putInt("fontcolor", this.mFontColor);
		editor.putInt("alpha", this.mAlpha);
		editor.putInt("textsize", this.mTextSize);
		editor.putInt("padding", this.mPadding);
	}
	public Paint getPaint() {
		if (mPaint!=null){
			return mPaint;
		}
		mPaint = new Paint();
		mPaint.setARGB(this.mAlpha, Color.red(this.mFontColor), Color
				.green(this.mFontColor), Color.blue(this.mFontColor));
		mPaint.setTextSize(this.mTextSize);
		mPaint.setColor(this.mFontColor);
		mPaint.setAntiAlias(true);
		mPaint.setTypeface(Typeface.MONOSPACE);
		return mPaint;
	}
}

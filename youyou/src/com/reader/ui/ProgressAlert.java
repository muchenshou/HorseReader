/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.reader.app.R;
import com.reader.app.ReadingActivity;
import com.reader.config.PageConfig;

public class ProgressAlert extends PopupWindow implements
		SeekBar.OnSeekBarChangeListener {

	private SeekBar mProgress;
	private static final int MINIMUM_BACKLIGHT = 0;
	private static final int MAXIMUM_BACKLIGHT = 100;
	private ReadingActivity mContext;

	public ProgressAlert(Context context) {
		super(context);
		mContext = (ReadingActivity) context;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.progressalert, null);
		mProgress = (SeekBar) view.findViewById(R.id.progress);
		mProgress.setOnSeekBarChangeListener(this);
		mProgress.setMax(MAXIMUM_BACKLIGHT - MINIMUM_BACKLIGHT);
		mProgress.setProgress(PageConfig.getTextSize());
		this.setContentView(view);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}

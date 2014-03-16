package com.reader.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reader.animation.AnimationView;
import com.reader.animation.IAnimation;
import com.reader.animation.SimpleAnimationView;
import com.reader.util.BitmapUtil;

public class PageView extends ViewGroup implements IAnimation{
	protected AnimationView _animationView;
	protected TextView _textView;
	protected TextView _timeView;
	protected ImageView _batteryview;
	public int _pageindex = 0;
	private Timer _timer;
	private Handler _uihandler;
	class TimeViewTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			_uihandler.post(new Runnable(){

				@Override
				public void run() {
					SimpleDateFormat f = new SimpleDateFormat("HH:mm",Locale.CHINA);
					
					_timeView.setText(f.format(new Date()));
				}
				
			});
		}
		
	}
	public PageView(Context context) {
		super(context);
		_uihandler = new Handler(Looper.getMainLooper());
		_animationView = new SimpleAnimationView(context,this);
		
		_textView = new TextView(context);
		_textView.setTextColor(Color.BLACK);
		_textView.setGravity(Gravity.LEFT);
		_textView.setPadding(20, 0, 0, 0);
		_textView.setText(""+_pageindex);
		
		_timeView = new TextView(context);
		
		_timeView.setTextColor(Color.BLACK);
		_timeView.setGravity(Gravity.RIGHT);
		_timeView.setPadding(0, 0, 20, 0);
		_timer = new Timer();
		_timer.schedule(new TimeViewTimerTask(), 1000,2000);
		
		_batteryview = new ImageView(context);
		Bitmap b = Bitmap.createBitmap(100, 50, Config.ARGB_8888);
		BitmapUtil.DrawBatteryBitmap(b, 0, 0);
		_batteryview.setImageBitmap(b);
		
		addView(_animationView);
		addView(_textView);
		addView(_timeView);
		addView(_batteryview);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		_animationView.layout(l, t, r, b);
		_textView.layout(l,b-50, r, b);
		_timeView.layout(l,b-50, r, b);
		_batteryview.layout(l,b-50, r, b);
	}

	@Override
	public int startAnimation(int flags) {
		_textView.setText(""+_pageindex);

		return 0;
	}

	@Override
	public int endAnimation(DIR flags) {
		// TODO Auto-generated method stub
		_textView.setText(""+_pageindex);
		return 0;
	}

}

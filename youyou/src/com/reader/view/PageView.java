package com.reader.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reader.animation.AnimationView;
import com.reader.animation.IAnimation;
import com.reader.animation.SimpleAnimationView;

public class PageView extends ViewGroup implements IAnimation{
	protected AnimationView _animationView;
	protected TextView _textView;
	protected TextView _timeView;
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
					Time t = new Time();
					t.set(System.currentTimeMillis());
					Log.i("song","timer shechedul");
					_timeView.setText(t.format2445());
					
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
		_textView.setText(""+_pageindex);
		
		_timeView = new TextView(context);
		
		_timeView.setTextColor(Color.BLACK);
		_timeView.setGravity(Gravity.RIGHT);
		_timer = new Timer();
		_timer.schedule(new TimeViewTimerTask(), 1000,2000);
		
		addView(_animationView);
		addView(_textView);
		addView(_timeView);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		_animationView.layout(l, t, r, b);
		_textView.layout(l,b-50, r, b);
		_textView.layout(l,b-50, r, b);
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

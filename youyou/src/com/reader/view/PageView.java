package com.reader.view;

import com.reader.animation.AnimationView;
import com.reader.animation.IAnimation;
import com.reader.animation.SimpleAnimationView;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

public class PageView extends ViewGroup implements IAnimation{
	protected AnimationView _animationView;
	protected TextView _textView;
	public int _pageindex = 0;
	public PageView(Context context) {
		super(context);
		_animationView = new SimpleAnimationView(context,this);
		_textView = new TextView(context);
		_textView.setTextColor(Color.BLACK);
		_textView.setGravity(Gravity.CENTER);
		_textView.setText(""+_pageindex);
		addView(_animationView);
		addView(_textView);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		_animationView.layout(l, t, r, b);
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

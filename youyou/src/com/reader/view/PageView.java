package com.reader.view;

import com.reader.animation.AnimationView;
import com.reader.animation.IAnimation;
import com.reader.animation.SimpleAnimationView;

import android.content.Context;
import android.view.ViewGroup;

public class PageView extends ViewGroup implements IAnimation{
	protected AnimationView _animationView;
	public PageView(Context context) {
		super(context);
		_animationView = new SimpleAnimationView(context,this);
		addView(_animationView);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i=0; i<getChildCount(); i++) {
			getChildAt(i).layout(l, t, r, b);
		}
	}

	@Override
	public int startAnimation(int flags) {
		return 0;
	}

	@Override
	public int endAnimation(DIR flags) {
		// TODO Auto-generated method stub
		return 0;
	}

}

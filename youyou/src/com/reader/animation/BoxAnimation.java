package com.reader.animation;

import android.content.Context;
import android.view.MotionEvent;

public class BoxAnimation extends AnimationView{

	public BoxAnimation(Context context, IAnimation animation) {
		super(context, animation);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

}

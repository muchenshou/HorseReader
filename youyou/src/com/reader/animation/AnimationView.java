package com.reader.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

/**
 * @author song
 * 这个类的作用是根据手势决定向前还是向后翻页，并绘制翻页时的动画，
 * 动画不同，则手势不同，由子类实现
 * 
 */
public class AnimationView extends View {
	Bitmap[] _bitmaps;
	IAnimation _animation;
	public AnimationView(Context context,IAnimation animation) {
		super(context);
		// TODO Auto-generated constructor stub
		_animation = animation;
	}

	public void setBitmapArray(Bitmap bits[]) {
		_bitmaps = bits;
	}
}

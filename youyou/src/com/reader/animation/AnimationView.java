package com.reader.animation;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

/**
 * @author song
 * �����������Ǹ������ƾ�����ǰ�������ҳ�������Ʒ�ҳʱ�Ķ�����
 * ������ͬ�������Ʋ�ͬ��������ʵ��
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

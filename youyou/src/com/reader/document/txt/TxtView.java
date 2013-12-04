package com.reader.document.txt;

import com.reader.view.PageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class TxtView extends PageView{
	private Bitmap b;
	public TxtView(Context context,TxtPageProvider provider) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}

	public void setBitmap(Bitmap b) {
		this._animationView.setBitmapArray(new Bitmap[]{b,b,b});
		_animationView.postInvalidate();
	}
}

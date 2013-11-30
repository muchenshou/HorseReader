package com.reader.document.txt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class TxtView extends View{
	public Bitmap b;
	public TxtView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (b!=null)
		canvas.drawBitmap(b, 0, 0, new Paint());
	}

}

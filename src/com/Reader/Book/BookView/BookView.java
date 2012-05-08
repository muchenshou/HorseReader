package com.Reader.Book.BookView;


import java.io.IOException;

import com.Reader.Book.Manager.TextUtil;
import com.Reader.Main.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class BookView extends PageWidget {
	protected int bookSize;
	protected byte bookContent;
	protected byte[] content;
	protected int padding = 5;
	private TextUtil textUtil = null;
	public Bitmap mCurPageBitmap, mNextPageBitmap;
	public Canvas mCurPageCanvas, mNextPageCanvas;

	public BookView(Context context) {
		super(context);
	}
	public void setTextUtil(TextUtil t){
		textUtil = t;
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		mCurPageBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap
				.createBitmap(w, h, Bitmap.Config.ARGB_8888);

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);

		try {
			textUtil.DrawText(mCurPageCanvas);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setBitmaps(mCurPageBitmap, mCurPageBitmap);
	}
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		/*Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		//Log.d("width height", "" + getWidth() + getHeight());
		try {
			textUtil.DrawText(canvas);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
}

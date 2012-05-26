package com.Reader.Book.BookView;

import java.io.IOException;

import com.Reader.Book.Manager.TextUtil;
//import com.Reader.Main.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

public class BookView extends PageWidget {
	protected int bookSize;
	protected byte bookContent;
	protected byte[] content;
	protected int padding = 5;
	private TextUtil textUtil = null;
	// public Bitmap mCurPageBitmap, mNextPageBitmap;
	

	public BookView(Context context) {
		super(context);
	}

	public void setTextUtil(TextUtil t) {
		textUtil = t;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Canvas mCurPageCanvas, mNextPageCanvas;
		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		Bitmap BG = this.textUtil.m_book_bg;

		int bitmap_w = BG.getWidth();
		int bitmap_h = BG.getHeight();
		Log.i("[BookView]",""+bitmap_w + " " +bitmap_h);
		Matrix m = new Matrix();
		m.postScale((float)w / (float)bitmap_w, (float)h / (float)bitmap_h);
		this.textUtil.m_book_bg = Bitmap.createBitmap(BG, 0, 0, bitmap_w,
				bitmap_h, m, true);
		try {
			textUtil.DrawText(mCurPageCanvas);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setBitmaps(mCurPageBitmap, mNextPageBitmap);
	}
}

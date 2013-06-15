package com.reader.view;

import com.reader.book.manager.BookContent;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;

public class BookSimpleView extends ViewGroup {
	public BookContent mBookContent;
	public Bitmap m_book_bg = null;

	public BookSimpleView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub

	}

	public void setBgBitmap(Bitmap BG) {
		m_book_bg = BG;
	}

}

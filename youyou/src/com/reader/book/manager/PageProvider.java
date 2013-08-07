/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.manager;

import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Paint;

import com.reader.book.Page;
import com.reader.book.model.BookModel;
import com.reader.config.PageConfig;

public class PageProvider {
	int mStart = 0;
	int mEnd = 0;
	public int pageline = 5;
	private float pageHeight = (float) 0.0;
	private Paint mPaint = null;
	BookModel mBookModel;
	private CopyOnWriteArrayList<Page> mPages = new CopyOnWriteArrayList<Page>();

	public PageProvider(BookModel bookModel) {
		mBookModel = bookModel;
		this.mPaint = PageConfig.pagePaintFromConfig(false);
	}

	public void update(int w, int h) {
		pageHeight = h;
		pageWidth = w;
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void update() {
		int lineHeight = PageConfig.getTextHeight(mPaint)
				+ PageConfig.getPadding();
		pageline = (int) (pageHeight / lineHeight);
	}

	public Page mCurPage;
	public int mCurIndex = 0;

	public Page getNextPage() {
		return mPages.get(mCurIndex < mPages.size() - 1 ? mCurIndex + 1
				: mCurIndex);
	}

	public Page getPrePage() {
		return mPages.get(mCurIndex > 0 ? mCurIndex - 1 : 0);
	}

	public void turnToPre() {
		if (mCurIndex > 0)
			mCurIndex--;
	}

	public void turnToNext() {
		if (mCurIndex < mPages.size() - 1)
			mCurIndex++;
	}

	public Page getCurPage() {
		return mPages.get(mCurIndex);
	}

	public BookPosition getCurPosition() {
		return null;
	}

}
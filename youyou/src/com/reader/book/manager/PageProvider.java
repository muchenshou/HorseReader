/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.manager;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Paint;
import android.util.Log;

import com.reader.book.Book;
import com.reader.book.Page;
import com.reader.book.model.BookModel;
import com.reader.config.PageConfig;

public class PageProvider {
	int mStart = 0;
	int mEnd = 0;
	public int pageline = 5;
	private float pageWidth = (float) 0.0;
	private float pageHeight = (float) 0.0;
	private Paint mPaint = null;
	BookModel mBookModel;
	public Book mBook = null;
	private List<Page> mPages = new ArrayList<Page>();

	public PageProvider(Book book) {
		mBook = book;
		mBookModel = new BookModel(mBook);
		this.mPaint = PageConfig.pagePaintFromConfig(false);
	}

	public void update(int w, int h) {
		pageHeight = h;
		pageWidth = w;
		mBookModel.pushIntoPagesList(mPages);
	}

	public void update() {
		int lineHeight = PageConfig.getTextHeight(mPaint) + PageConfig.getPadding();
		pageline = (int) (pageHeight / lineHeight);
	}

	public Page mCurPage;
	public int mCurIndex = 0;

	public void setCurPosition(BookPosition cur) {
		// mCurPage = getPage(cur);
	}

	public Page getNextPage() {
		Log.i("hello", "getNextPage");
		return mPages.get(mCurIndex < mPages.size() - 1 ? mCurIndex + 1
				: mCurIndex);
	}

	public Page getPrePage() {
		return mPages.get(mCurIndex > 0 ? mCurIndex - 1 : 0);
	}

	public void turnToPre() {
		// setCurPosition(new BookPosition(getprePagePosition()));
		if (mCurIndex > 0)
			mCurIndex--;
	}

	public void turnToNext() {
		Log.i("hello","trunToNext page size:"+mPages.size());
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
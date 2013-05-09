package com.reader.book.bookview;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.reader.book.Book;
import com.reader.book.manager.BookContent;
import com.reader.config.PageConfig;

public class PageDisplay {
	public static PageDisplay Instance;
	private BookContent mBookContent;
	private PageConfig mPageConfig;
	private Paint mPaint;
	private Book mBook;
	private Bitmap mfrontPageBitmap;
	private Bitmap mBackPageBitmap;
	private TimeObj mTimeObj;
	private PageObj mPageObj = null;
	private BookNameObj mBookNameObj = null;
	private BookProgressObj mBookProgressObj;
	public Bitmap m_book_bg = null;
	private int m_backColor = 0xffff9e85;
	public static int CUR = 0;
	public static int PRE = 1;
	public static int NEXT = 2;

	public PageDisplay(BookContent bookContent) {
		mBookContent = bookContent;
		mPageConfig = mBookContent.mPageConfig;
		mPaint = mPageConfig.getPaint();
		mBook = mBookContent.mBook;

		mPageObj = new PageObj(mBookContent, mBook);
		mTimeObj = new TimeObj();

		mBookNameObj = new BookNameObj();
		mBookNameObj.setBookName(mBook.getName());

		mBookProgressObj = new BookProgressObj(this.mBookContent, mBook.size());
		Instance = this;
	}

	public void Draw(Canvas canvas) {
		if (m_book_bg == null)
			canvas.drawColor(m_backColor);
		else
			canvas.drawBitmap(m_book_bg, 0, 0, null);
		mPageObj.Draw(canvas, this.mPaint);
		mTimeObj.Draw(canvas, this.mPageConfig.getOthersPaint());
		mBookNameObj.Draw(canvas, this.mPaint);
		mBookProgressObj.Draw(canvas, this.mPageConfig.getOthersPaint());
	}

	public void init(int w, int h) {
		mfrontPageBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mBackPageBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		float len = mBookNameObj.getNameMeasure(mPageConfig.getOthersPaint());
		this.mBookNameObj.setPosition((w - (int) len) / 2, h - 5);
		//
		this.mBookProgressObj.setPosition(5, h - 5);
		//
		len = mPageConfig.getOthersPaint().measureText("00:00");
		mTimeObj.setPosition(w - (int) len - 5, h - 5);
	}

	public void setBg(Bitmap bg) {
		this.m_book_bg = bg;
	}

	public Bitmap tranlateFrontBitmap(List<String> page) {
		Canvas c = new Canvas(mfrontPageBitmap);
		mPageObj.setPageString(page);
		Draw(c);
		return mfrontPageBitmap;
	}

	public Bitmap tranlateBackBitmap(List<String> page) {
		Canvas c = new Canvas(mBackPageBitmap);
		mPageObj.setPageString(page);
		Draw(c);
		return mBackPageBitmap;
	}

	public Bitmap tranlateFrontBitmap() {
		List<String> page = mBookContent.getCurPage();
		Canvas c = new Canvas(mfrontPageBitmap);
		mPageObj.setPageString(page);
		Draw(c);
		return mfrontPageBitmap;
	}

	public Bitmap tranlateBackBitmap(int dir) {
		List<String> page = dir == PRE ? mBookContent.getPrePage():mBookContent.getNextPage();
		Canvas c = new Canvas(mBackPageBitmap);
		mPageObj.setPageString(page);
		Draw(c);
		return mBackPageBitmap;
	}
}

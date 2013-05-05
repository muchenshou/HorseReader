/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.bookview;

import com.reader.book.Book;
import com.reader.book.manager.BookContent;
import com.reader.config.PageConfig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class BookView extends View implements View.OnTouchListener {
	protected int bookSize;
	protected byte bookContent;
	protected byte[] content;
	protected int padding = 5;
	public BookContent mBookContent;
	private BookViewAnimation mAnimation;
	public Bitmap m_book_bg = null;
	Book mBook;
	private int m_backColor = 0xffff9e85;
	private boolean mInit = false;
	private PageConfig mPageConfig;
	private TimeObj mTimeObj;
	private PageObj mPageObj = null;
	private BookNameObj mBookNameObj = null;
	private BookProgressObj mBookProgressObj;
	private Paint mPaint;

	Bitmap mCurPageBitmap = null;
	Bitmap mNextPageBitmap = null;
	Canvas mCurPageCanvas;
	Canvas mNextPageCanvas;

	public BookView(Context context, Book book) {
		super(context);
		setOnTouchListener(this);
		this.mBook = book;
		mPageConfig = new PageConfig(context);
		mPaint = mPageConfig.getPaint();
		mBookContent = new BookContent(book, mPageConfig);

		mPageObj = new PageObj(this, book);
		mTimeObj = new TimeObj();

		mBookNameObj = new BookNameObj();
		mBookNameObj.setBookName(book.getName());

		mBookProgressObj = new BookProgressObj(this.mBookContent, book.size());

		this.mAnimation = new SimulateTurnPage(getContext());
		//this.mAnimation = new NoTurnAnimation(getContext());
		this.mAnimation.setBookView(this);
	}

	public PageConfig getPageConfig() {
		return this.mPageConfig;
	}

	public Paint getPaint() {
		return this.mPaint;
	}

	public void setTextSize(int size) {
		this.mPageConfig.setTextSize(size);
	}

	public static int getTextHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();//
		return (int) (Math.ceil(fm.descent - fm.top) + 1);
	}

	public void setTextUtil(PageObj t) {
		mPageObj = t;
	}

	public void setBgBitmap(Bitmap BG) {
		m_book_bg = BG;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mCurPageBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);

		this.mAnimation.onSizeChange(w, h, oldw, oldh);

		mBookContent.update(w - 20,
				h - BookView.getTextHeight(this.mPageConfig.getOthersPaint())
						- 20);
		//
		float len = mBookNameObj.getNameMeasure(mPageConfig.getOthersPaint());
		this.mBookNameObj.setPosition((w - (int) len) / 2, h - 5);
		//
		this.mBookProgressObj.setPosition(5, h - 5);
		//
		len = mPageConfig.getOthersPaint().measureText("00:00");
		mTimeObj.setPosition(w - (int) len -5, h - 5);

		Bitmap BG = this.m_book_bg;

		int bitmap_w = BG.getWidth();
		int bitmap_h = BG.getHeight();
		Log.i("[BookView]", "" + bitmap_w + " " + bitmap_h);
		Matrix m = new Matrix();
		m.postScale((float) w / (float) bitmap_w, (float) h / (float) bitmap_h);
		this.m_book_bg = Bitmap.createBitmap(BG, 0, 0, bitmap_w, bitmap_h, m,
				true);

		this.update();
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

	@Override
	protected void onDraw(Canvas canvas) {
		if (mAnimation.state() == BookViewAnimation.NONE) {
			canvas.drawBitmap(mCurPageBitmap, 0, 0, mPaint);
			return;
		}
		if (mAnimation.state() == BookViewAnimation.STATE_ANIMATION_END) {
			if (mAnimation.DragToRight())
				mBookContent.turnToPre();
			else
				mBookContent.turnToNext();
			
			mPageObj.setPageString(mBookContent.getCurPage());
			Draw(mCurPageCanvas);
			canvas.drawBitmap(mCurPageBitmap, 0, 0, mPaint);
			mAnimation.setState(BookViewAnimation.NONE);
			postInvalidate();
			return;
		}
		this.mAnimation.onDraw(canvas);
	}

	public void update() {
		mBookContent.update();
		if (this.mInit == false) {
			mBookContent.setCurPosition(mBook.openOffset);
			this.mPageObj.setPageString(mBookContent.getCurPage());
			mInit = true;
		} else {
			this.mPageObj.setPageString(mBookContent.getCurPage());
		}
		Draw(mCurPageCanvas);
		mAnimation.setCurBitmap(mCurPageBitmap);
		this.mAnimation.update();
		postInvalidate();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Rect rect = new Rect(0, 0, v.getWidth(), v.getHeight());
		if (event.getAction() == MotionEvent.ACTION_DOWN
				&& event.getX() < rect.exactCenterX() + 40
				&& event.getX() > rect.exactCenterX() - 40
				&& event.getY() > rect.exactCenterY() - 20
				&& event.getY() < rect.exactCenterY() + 20) { // gridview
			return false;
		}
		mAnimation.onTouch(v, event);
		if (mAnimation.state() == BookViewAnimation.STATE_TOUCH_START) {
			// Draw current page and Set
			mPageObj.setPageString(mBookContent.getCurPage());
			Draw(mCurPageCanvas);
			mAnimation.setCurBitmap(mCurPageBitmap);
			
			// Draw next or pre page and Set it
			if (mAnimation.DragToRight())
				mPageObj.setPageString(mBookContent.getPrePage());
			else
				mPageObj.setPageString(mBookContent.getNextPage());
			Draw(mNextPageCanvas);
			mAnimation.setNextBitmap(mNextPageBitmap);
		}
		postInvalidate();
		return true;
	}

	public void setTurnAnimation(BookViewAnimation noTurnAnimation) {
		this.mAnimation = noTurnAnimation;
		mAnimation.setBookView(this);
		this.mAnimation.onSizeChange(getWidth(), getHeight(), 0, 0);
	}
}

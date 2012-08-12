/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */package com.Reader.Book.BookView;

import com.Reader.Book.Book;
import com.Reader.Book.Manager.BookManager;
import com.Reader.Book.Manager.BookReading;
import com.Reader.Config.PageConfig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class BookView extends View implements View.OnTouchListener {
	protected int bookSize;
	protected byte bookContent;
	protected byte[] content;
	protected int padding = 5;
	BookManager mBookManager;
	public BookReading bookreading;
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

	Bitmap mCurPageBitmap = null; // µ±Ç°Ò³
	Bitmap mNextPageBitmap = null;
	Canvas mCurPageCanvas;
	Canvas mNextPageCanvas;
	public BookView(Context context, Book book) {
		super(context);
		setOnTouchListener(this);
		this.mBook = book;
		mPageConfig = new PageConfig(context);
		mPaint = mPageConfig.getPaint();
		bookreading = new BookReading(book, this);

		mPageObj = new PageObj(this, book);
		mTimeObj = new TimeObj();

		mBookNameObj = new BookNameObj();
		mBookNameObj.setBookName(book.getName());

		mBookProgressObj = new BookProgressObj(this.bookreading, book.size());
		
		this.mAnimation = new PageWidget(getContext());
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
		mCurPageBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		this.mAnimation.AfterSizeChange(w, h, oldw, oldh);
		
		bookreading.update(w-20,
				h - BookView.getTextHeight(this.mPageConfig.getOthersPaint())
						- 20);
		//
		float len = mBookNameObj.getNameMeasure(mPageConfig.getOthersPaint());
		this.mBookNameObj.setPosition((w - (int) len) / 2, h - 5);
		//
		this.mBookProgressObj.setPosition(0, h - 5);
		//
		len = mPageConfig.getOthersPaint().measureText("00:00");
		mTimeObj.setPosition(w - (int) len, h - 5);

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
	public void computeScroll(){
		super.computeScroll();
		this.mAnimation.computeScroll();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		this.mAnimation.AfterDraw(canvas);
	}
	public void update() {
		bookreading.update();
		if (this.mInit == false) {
			this.mPageObj.setLocal(mBook.openOffset);
			mInit = true;
		} else {
			this.mPageObj.setLocal(bookreading.getCurPosition());
		}

		Draw(mCurPageCanvas);
		this.mAnimation.update();
		postInvalidate();
	}

	public boolean onTouch(View v, MotionEvent event) {
		return this.mAnimation.AfterTouch(v, event);
	}

	public void nextLine() {
		this.mPageObj.setPageString(this.bookreading.nextLine());
	}

	public void nextPage() {
		this.mPageObj.setPageString(this.bookreading.nextPage());

	}

	public void preLine() {
		this.mPageObj.setPageString(this.bookreading.preLine());
	}

	public void prePage() {
		this.mPageObj.setPageString(this.bookreading.prePage());
	}

	public void setLocal(int offset) {
		this.bookreading.getPageStr(offset);
		this.update();
	}
}

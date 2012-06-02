package com.Reader.Book.BookView;

import com.Reader.Book.Book;
import com.Reader.Book.Manager.BookManager;
import com.Reader.Book.Manager.BookReading;
import com.Reader.Config.PageConfig;
import com.Reader.Main.ReadingActivity;

//import com.Reader.Main.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class BookView extends PageWidget implements View.OnTouchListener {
	protected int bookSize;
	protected byte bookContent;
	protected byte[] content;
	protected int padding = 5;
	BookManager mBookManager;
	public BookReading bookreading;
	
	public Bitmap m_book_bg = null;
	Book mBook;
	private int m_backColor = 0xffff9e85;
	private boolean mInit = false;
	private PageConfig mPageConfig;
	private TimeObj mTimeObj;
	private PageObj mPageObj = null;
	private BookNameObj mBookNameObj = null;
	private BookProgressObj mBookProgressObj;
	public BookView(Context context, Book book) {
		super(context);
		setOnTouchListener(this);
		this.mBook = book;
		mPageConfig = new PageConfig(context);
		mPaint = mPageConfig.getPaint();
		bookreading = new BookReading(book,this);
		
		
		mPageObj = new PageObj(this, book);
		mTimeObj = new TimeObj();
		
		mBookNameObj = new BookNameObj();
		mBookNameObj.setBookName(book.getName());
		
		mBookProgressObj = new BookProgressObj(this.bookreading, book.size());
	}
	
	public PageConfig getPageConfig(){
		return this.mPageConfig;
	}
	public Paint getPaint(){
		return this.mPaint;
	}
	public void setTextSize(int size) {
		this.mPageConfig.mTextSize = size;
	}

	public static int getTextHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();//
		return (int) (Math.ceil(fm.descent - fm.top));
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
		
		bookreading.update(w,h);
		
		
		//
		this.mBookNameObj.setPosition(0, h);
		//
		this.mBookProgressObj.setPosition(w/2-80, h);
		//
		mTimeObj.setPosition(w/2 + 80, h);

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
		mTimeObj.Draw(canvas, this.mPaint);
		//mBookNameObj.Draw(canvas, this.mPaint);
		mBookProgressObj.Draw(canvas, mPaint);
	}
	public void update(){
		bookreading.update();
		//bookreading.getPageStr(bookreading.getCurPosition());
		//
		if (this.mInit == false){
			this.mPageObj.setLocal(mBook.openOffset);
			mInit = true;
		}else{
			this.mPageObj.setLocal(bookreading.getCurPosition());
		}
		Canvas mCurPageCanvas;
		mCurPageCanvas = new Canvas(mCurPageBitmap);
		Draw(mCurPageCanvas);
		this.setBitmaps(mCurPageBitmap, mNextPageBitmap);
		postInvalidate();
	}
	public boolean onTouch(View v, MotionEvent event) {

		Rect rect = new Rect(0, 0, v.getWidth(), v.getHeight());

		if (event.getAction() == MotionEvent.ACTION_DOWN
				&& event.getX() < rect.exactCenterX() + 40
				&& event.getX() > rect.exactCenterX() - 40
				&& event.getY() > rect.exactCenterY() - 20
				&& event.getY() < rect.exactCenterY() + 20) { // gridview
			((ReadingActivity) this.getContext()).setBookSet();
			return false;
		}

		boolean ret = false;

		Canvas mCurPageCanvas, mNextPageCanvas;
		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		if (v == this) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				abortAnimation();
				calcCornerXY(event.getX(), event.getY());
				this.Draw(mCurPageCanvas);
				if (DragToRight()) {
					this.mPageObj.nextPage();
					this.Draw(mNextPageCanvas);

				} else {
					this.mPageObj.nextPage();
					this.Draw(mNextPageCanvas);
				}
			}

			ret = doTouchEvent(event);
			return ret;
		}

		return false;
	}
	

}
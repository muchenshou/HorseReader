/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.bookview;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

import com.reader.book.Book;
import com.reader.book.manager.BookContent;
import com.reader.config.PageConfig;

public class BookView extends View implements View.OnTouchListener {
	protected int bookSize;
	protected byte bookContent;
	protected byte[] content;
	protected int padding = 5;
	public BookContent mBookContent;
	private BookViewAnimation mAnimation;
	public Bitmap m_book_bg = null;
	Book mBook;
	private boolean mInit = false;
	BookScreenDisplay mBookScreenDisplay;
	private Paint mPaint;
	public static BookView Instance;
	public BookView(Context context, Book book) {
		super(context);
		Instance = this;
		setOnTouchListener(this);
		this.mBook = book;
		mPaint = PageConfig.pagePaintFromConfig(false);
		mBookContent = new BookContent(book);

		mBookScreenDisplay = new BookScreenDisplay(mBookContent);
		// this.mAnimation = new SimulateTurnPage(getContext());
		// this.mAnimation = new NoTurnAnimation(getContext());
		this.mAnimation = new SimpleAnimation(getContext());
		this.mAnimation.setBookView(this);
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.start();
	}

	public Paint getPaint() {
		return this.mPaint;
	}

	public static int getTextHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();//
		return (int) (Math.ceil(fm.descent - fm.top) + 1);
	}

	public void setBgBitmap(Bitmap BG) {
		m_book_bg = BG;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.mAnimation.onSizeChange(w, h, oldw, oldh);

		mBookContent.update(w - 20,
				h - BookView.getTextHeight(PageConfig.getOthersPaint(false))
						- 20);
		//
		mBookScreenDisplay.init(getWidth(), getHeight());

		Bitmap BG = this.m_book_bg;
		int bitmap_w = BG.getWidth();
		int bitmap_h = BG.getHeight();
		Log.i("[BookView]", "" + bitmap_w + " " + bitmap_h);
		Matrix m = new Matrix();
		m.postScale((float) w / (float) bitmap_w, (float) h / (float) bitmap_h);
		mBookScreenDisplay.setBg(Bitmap.createBitmap(BG, 0, 0, bitmap_w, bitmap_h, m,
				true));
		this.reset();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mAnimation.state() == BookViewAnimation.NONE) {
			canvas.drawBitmap(
					mBookScreenDisplay.tranlateFrontBitmap(mBookContent.getCurPage()),
					0, 0, mPaint);
			return;
		}
		if (mAnimation.state() == BookViewAnimation.STATE_ANIMATION_END) {
			if (mAnimation.DragToRight())
				mBookContent.turnToPre();
			else
				mBookContent.turnToNext();

			canvas.drawBitmap(
					mBookScreenDisplay.tranlateFrontBitmap(mBookContent.getCurPage()),
					0, 0, mPaint);

			mAnimation.setState(BookViewAnimation.NONE);
			lock.lock();
			con.signal();
			lock.unlock();
			postInvalidate();
			return;
		}
		this.mAnimation.onDraw(canvas);
	}

	private void reset() {
		mBookContent.update();
		Log.i("hello","bookview:reset");
		if (this.mInit == false) {
			//mBookContent.setCurPosition(mBook.openOffset);
			mInit = true;
		}
		mAnimation.setFrontBitmap(mBookScreenDisplay.tranlateFrontBitmap(mBookContent
				.getCurPage()));
		this.mAnimation.update();
		postInvalidate();
	}

	long filterPoint = 0;

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
		if (event.getAction() == MotionEvent.ACTION_DOWN
				&& mAnimation.state() != BookViewAnimation.NONE) {
			filterPoint = event.getDownTime();
		}
		if (filterPoint == event.getDownTime()) {
			return false;
		}
		mAnimation.onTouch(v, event);
		if (mAnimation.state() == BookViewAnimation.STATE_TOUCH_START) {

		}
		postInvalidate();
		return true;
	}

	public void setTurnAnimation(BookViewAnimation noTurnAnimation) {
		this.mAnimation = noTurnAnimation;
		mAnimation.setBookView(this);
		this.mAnimation.onSizeChange(getWidth(), getHeight(), 0, 0);
	}

	Lock lock = new ReentrantLock();
	Condition con = lock.newCondition();
	Thread thread = new Thread() {

		@Override
		public void run() {
			while (true) {
				lock.lock();
				try {
					con.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
				if (mAnimation.DragToRight()) {
					mBookContent.getPrePage();
				} else {
					mBookContent.getNextPage();
				}
				lock.unlock();
			}

		}

	};
}

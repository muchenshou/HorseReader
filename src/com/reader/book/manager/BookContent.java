/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.manager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.graphics.Paint;
import android.util.Log;

import com.reader.book.Book;
import com.reader.book.CharInfo;
import com.reader.book.Line;
import com.reader.book.Page;
import com.reader.book.PageBuffer;
import com.reader.book.bookview.BookView;
import com.reader.config.PageConfig;

public class BookContent {
	int mStart = 0;
	int mEnd = 0;
	public int pageline = 5;
	private float pageWidth = (float) 0.0;
	private float pageHeight = (float) 0.0;
	private Paint mPaint = null;
	// Page mPage = new Page();
	PageBuffer mPageBuffer = new PageBuffer();
	public Book mBook = null;
	public PageConfig mPageConfig;

	public BookContent(Book book, PageConfig pageConfig) {
		mBook = book;
		mPageConfig = pageConfig;
		this.mPaint = mPageConfig.getPaint();
	}

	public String getCurContent() {
		if (mPageBuffer.isEmpty())
			return null;
		return mPageBuffer.existPage(mCurPosition).getStrings().get(0);
	}

	Line getLine(int start) {
		float[] widths = new float[1];
		char[] ch = new char[1];
		float widthTotal = (float) 0.0;
		Line line = new Line();
		line.mStart = start;
		while (true) {
			CharInfo charinfo = mBook.getChar(start);

			if (charinfo == null) {
				return null;
			}
			ch[0] = charinfo.character;
			start += charinfo.length;
			if (ch[0] == '\n') {
				line.mLength += charinfo.length;
				break;
			}

			mPaint.getTextWidths(ch, 0, 1, widths);
			widthTotal += Math.ceil(widths[0]);
			if (widthTotal > this.pageWidth) {
				break;
			}
			line.strLine.append(ch[0]);
			line.mLength += charinfo.length;
		}
		widthTotal = (float) 0.0;
		return line;

	}

	public int getCurPosition() {
		return mCurPosition;
	}

	public int getLineHeight() {
		return BookView.getTextHeight(mPaint) + this.mPageConfig.mPadding;
	}

	public void update(int w, int h) {
		pageHeight = h;
		pageWidth = w;
	}

	public void update() {
		pageline = (int) (pageHeight / getLineHeight());
	}

	private int mCurPosition = 0;

	public void setCurPosition(int cur) {
		mCurPosition = cur;
		getPageStr(mCurPosition);
	}

	public List<String> getCurPage() {
		return getPageStr(mCurPosition);
	}

	public List<String> getNextPage() {
		return getPageStr(getNextPagePosition());
	}

	public List<String> getPrePage() {
		return getPageStr(getprePagePosition());
	}

	public void turnToPre() {
		setCurPosition(getprePagePosition());
	}

	public void turnToNext() {
		setCurPosition(getNextPagePosition());
	}

	private synchronized List<String> getPageStr(int start) {
		if (!thread.isAlive()) {
			mCon = mLock.newCondition();
			thread.start();
		}
		if (!mPageBuffer.isEmpty()) {
			Page page;
			if ((page = mPageBuffer.existPage(start)) != null) {
				mLock.lock();
				mCon.signal();
				mLock.unlock();
				return page.getStrings();
			}
		}
		final Page page = new Page();
		page.clear();
		for (; page.getLinesSize() < pageline;) {
			if (page.getLinesSize() == 0) {

				if (this.getLine(start) == null) {
					break;
				}
				page.addLine(this.getLine(start));
			} else {
				if (this.getLine(page.getPageEndPosition() + 1) == null) {
					break;
				}
				page.addLine(this.getLine(page.getPageEndPosition() + 1));
			}
		}

		mPageBuffer.addPage(mPageBuffer.addPage(page));
		mLock.lock();
		mCon.signal();
		mLock.unlock();
		return page.getStrings();
	}

	public int getNextPagePosition() {
		if (!mPageBuffer.isEmpty()) {
			return mPageBuffer.existPage(mCurPosition).getPageEndPosition() + 1;
		}
		return 0;
	}

	/**
	 * when page turn for prevent page,this variable saved all lines
	 * */
	private LinkedList<Line> mBufLine = new LinkedList<Line>();

	private int preLinePosition(int s) {
		int start = s;
		if (start <= 0)
			return 0;
		int savevalue = start;
		CharInfo charinfo = this.mBook.getPreChar(start);
		if (charinfo.character == '\n') {
			start -= charinfo.length;
		}

		charinfo = this.mBook.getPreChar(start);
		start -= charinfo.length;
		while (charinfo.character != '\n') {
			if (start <= 0)
				return 0;
			charinfo = this.mBook.getPreChar(start);
			start -= charinfo.length;
		}
		Line line = this.getLine(start + charinfo.length);

		start += line.mLength;
		this.mBufLine.clear();

		Log.i("prelinenum", "sa:" + savevalue);
		while (line.mStart < savevalue) {
			mBufLine.add(line);
			line = this.getLine(start);
			if (line == null)
				break;
			start += line.mLength;
		}
		Log.i("prelinenum", "start:" + line.mStart);
		return 0;
	}

	public boolean isBookEnd() {
		return false;
	}

	public boolean isBookStart() {
		if (mPageBuffer.isEmpty())
			return true;
		Page page = mPageBuffer.existPage(mCurPosition);
		if (page.getLinesSize() == 0)
			return true;
		if (page.getPageEndPosition() == 0)
			return true;
		return false;
	}

	public int getprePagePosition() {
		int curPosition = mCurPosition;
		Page page;
		if ((page = mPageBuffer.existPrePage(curPosition)) != null) {
			return page.getPageStartPosition();
		}
		LinkedList<Line> prepage = new LinkedList<Line>();
		if (this.isBookStart()) {
			return 0;
		}
		this.preLinePosition(curPosition);
		prepage.addAll(0, this.mBufLine);
		for (; prepage.size() < this.pageline && !isBookEnd();) {
			preLinePosition(prepage.get(0).mStart);
			for (Line l : mBufLine) {
				Log.i("[prepage]", l.strLine.toString());
			}
			prepage.addAll(0, this.mBufLine);
		}
		page = new Page();
		page.clear();
		for (Line l : prepage) {
			page.addLine(l);
		}

		mPageBuffer.addPage(page);
		return page.getPageStartPosition();
	}

	Lock mLock = new ReentrantLock();
	Condition mCon;
	Thread thread = new Thread() {

		@Override
		public void run() {
			while (true) {
				try {
					mLock.lock();
					mCon.await();
					getNextPage();
					mLock.unlock();
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	};

}
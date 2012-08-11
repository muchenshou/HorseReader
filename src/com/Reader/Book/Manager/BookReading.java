/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.Reader.Book.Manager;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import com.Reader.Book.BookView.BookView;
import com.Reader.Book.Book;
import com.Reader.Book.CharInfo;

import android.graphics.Paint;
import android.util.Log;

public class BookReading {
	int mStart = 0;
	int mEnd = 0;
	public int pageline = 5;
	private float pageWidth = (float) 0.0;
	private float pageHeight = (float) 0.0;
	private Paint mPaint = null;
	Page mPage = new Page();
	public Book mBook = null;
	private BookView mBookView;

	public BookReading(Book book, BookView bookview) {
		mBook = book;
		mBookView = bookview;
		this.mPaint = mBookView.getPageConfig().getPaint();
	}

	public String getCurContent(){
		return mPage.getStrings().get(0);
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
		if (mPage.mLines.size() == 0)
			return 0;
		return mPage.mLines.get(0).mStart;
	}

	public int getLineHeight() {
		return BookView.getTextHeight(mPaint)
				+ this.mBookView.getPageConfig().mPadding;
	}

	public void update(int w, int h) {
		pageHeight = h;
		pageWidth = w;
	}

	public void update() {
		pageline = (int) (pageHeight / getLineHeight());
	}

	public List<String> getPageStr(int start) {
		mPage.mLines.clear();
		for (; mPage.mLines.size() < pageline;) {
			if (mPage.mLines.size() == 0) {

				if (this.getLine(start) == null) {
					break;
				}
				mPage.mLines.add(this.getLine(start));
			} else {
				if (this.getLine(mPage.mLines.get(mPage.mLines.size() - 1)
						.getEnd()) == null) {
					break;
				}
				mPage.mLines.add(this.getLine(mPage.mLines.get(
						mPage.mLines.size() - 1).getEnd()));
			}

		}

		return mPage.getStrings();
	}

	public List<String> nextLine() {
		mPage.mLines.remove(0);
		mPage.mLines.add(this.getLine(mPage.mLines.get(mPage.mLines.size() - 1)
				.getEnd()));
		return mPage.getStrings();
	}

	public List<String> nextPage() {
		if (isBookEnd()) {
			return mPage.getStrings();
		}
		int local = 0;
		try {
			local = mPage.mLines.getLast().getEnd();
		} catch (NoSuchElementException e) {
			return mPage.getStrings();
		}

		return this.getPageStr(local);
	}

	private LinkedList<Line> mBufLine = new LinkedList<Line>();

	private int preLineNum(int start) {
		Log.i("start local", "" + mPage.mLines.get(0).mStart);
		//int start = mPage.mLines.get(0).mStart;

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
		
		Log.i("prelinenum","sa:"+savevalue);
		while (line.mStart < savevalue) {
			mBufLine.add(line);
			line = this.getLine(start);
			if (line == null)
				break;
			start += line.mLength;
		}
		Log.i("prelinenum","start:"+line.mStart);
		return 0;
	}

	public List<String> preLine() {
		return null;
	}

	public boolean isBookEnd() {
		return false;
	}

	public boolean isBookStart(){
		if (mPage.mLines.size() == 0)
			return true;
		if (mPage.mLines.get(0).mStart == 0)
			return true;
		return false;
	}
	public List<String> prePage() {
		LinkedList<Line> prepage = new LinkedList<Line>();
		//int local = 0;
		if (this.isBookStart()){
			return mPage.getStrings();
		}
		this.preLineNum(this.mPage.mLines.get(0).mStart);
		prepage.addAll(0, this.mBufLine);
		for (;prepage.size()<this.pageline && !isBookEnd();) {
			preLineNum(prepage.get(0).mStart);
			for (Line l: mBufLine){
				//Log.i("[prepage]", l.strLine.toString());
			}
			
			prepage.addAll(0, this.mBufLine);
		}
		this.mPage.mLines.clear();
		this.mPage.mLines.addAll(0, prepage.subList(prepage.size()-pageline, prepage.size()-1));
		return  mPage.getStrings();
	}

	class Line {
		int mStart = 0;
		int mLength = 0;// bytes
		StringBuffer strLine = new StringBuffer();

		int getEnd() {
			return mStart + mLength;
		}
	}

	class Page {
		LinkedList<Line> mLines = new LinkedList<Line>();

		List<String> getStrings() {
			List<String> strs = new LinkedList<String>();
			for (int i = 0; i < mLines.size(); i++) {
				strs.add(mLines.get(i).strLine.toString());
			}
			return strs;
		}
	}
}
package com.Reader.Book.Manager;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import com.Reader.Book.Book;
import com.Reader.Book.CharInfo;
import com.Reader.Book.BookView.BookView;

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
		if (mPage.mLines.size() < this.pageline && mPage.mLines.size() > 0) {
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

	public int preLineNum() {
		if (mPage.mLines.get(0).mStart <= 0) {
			return 0;
		}

		Log.i("start local", "" + mPage.mLines.get(0).mStart);

		int start = mPage.mLines.get(0).mStart;
		if (start <= 0)
			return 0;
		CharInfo charinfo = this.mBook.getPreChar(start);
		if (charinfo.character == '\n') {
			start -= charinfo.length;
		}
		int savevalue = start;
		charinfo = this.mBook.getPreChar(start);
		start -= charinfo.length;
		while (charinfo.character != '\n') {
			if (start <= 0)
				return 0;
			charinfo = this.mBook.getPreChar(start);
			start -= charinfo.length;
		}
		Line saveline;
		Line line = this.getLine(start + charinfo.length);

		start += line.mLength;
		saveline = line;
		while (line.mStart < savevalue) {
			saveline = line;
			line = this.getLine(start + charinfo.length);
			if (line == null)
				break;
			start += line.mLength;
		}
		return saveline.mStart;

	}

	public List<String> preLine() {

		if (mPage.mLines.get(0).mStart == 0) {
			return mPage.getStrings();
		}
		int local = this.preLineNum();
		mPage.mLines.remove(mPage.mLines.size() - 1);

		mPage.mLines.addFirst(getLine(local));
		return mPage.getStrings();
	}

	public List<String> prePage() {
		int local = 0;
		for (int i = 0; i < this.pageline; i++) {
			if (mPage.mLines.get(0).mStart == 0) {
				local = 0;
			}
			local = this.preLineNum();
			this.getPageStr(local);
		}
		return this.getPageStr(local);
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
package com.Reader.Book.Manager;

import java.util.LinkedList;
import java.util.List;
import com.Reader.Book.Book;
import com.Reader.Book.CharInfo;
import android.graphics.Paint;
import android.util.Log;

public class BookReading {
	int mStart = 0;
	int mEnd = 0;
	public int pageline = 5;
	public float pageWidth = (float) 0.0;
	public Paint mPaint = null;
	Page mPage = new Page();
	public Book mBook = null;
	public BookReading(Book book){
		mBook = book;
	}
	Line getLine(int start) {
		Log.i("gc","here");
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
			if (ch[0] == 8233) {
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

	public int getCurLocal() {
		return mPage.mLines.get(0).mStart;
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
		for (int i=0; i< mPage.mLines.size();i++){
			Log.i("line", ""+mPage.mLines.get(i).mStart);
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
		if (mPage.mLines.size() < this.pageline && mPage.mLines.size() != 0) {
			return mPage.getStrings();
		}
		int local = mPage.mLines.getLast().getEnd();
		return this.getPageStr(local);
	}

	public int preLineNum() {
		if (mPage.mLines.get(0).mStart <= 0) {
			return 0;
		}

		
		// Log.d("start local", "" + start);
		float[] widths = new float[1];
		float widthTotal = (float) 0.0;
		char[] ch = new char[1];
		CharInfo charinfo = this.mBook.getPreChar(mPage.mLines.get(0).mStart);
		int start = mPage.mLines.get(0).mStart - charinfo.length;
		ch[0] = charinfo.character;
		if (ch[0] != 8233) {
			widthTotal = 0;
			mPaint.getTextWidths(ch, 0, 1, widths);
			widthTotal += Math.ceil(widths[0]);
			start -= charinfo.length;
			while (true) {
				if (start < 0)
					return 0;
				charinfo = this.mBook.getChar(start);
				ch[0] = charinfo.character;

				if (ch[0] == 8233) {
					break;
				}

				mPaint.getTextWidths(ch, 0, 1, widths);
				widthTotal += Math.ceil(widths[0]);
				if (widthTotal > this.pageWidth) {
					break;
				}
				start -= charinfo.length;
			}
			return start + charinfo.length;
		}

		if (ch[0] == 8233) {
			// Ãÿ ‚¥¶¿Ì
			// Log.d("is touch1", "touch");
			start -= charinfo.length;
			while (true) {
				if (start < 0)
					return 0;
				charinfo = this.mBook.getChar(start);
				// Log.d("start", ""+start);
				ch[0] = charinfo.character;
				// Log.d("char1", "" + ch[0]);
				if (ch[0] == 8233 || ch[0] == 0) {
					break;
				}
				start = start - charinfo.length;
			}
			start += charinfo.length;
			int num = 0;
			// Log.d("is touch2", "touch");
			while (true) {
				// Log.d("is touch3", "touch");
				charinfo = this.mBook.getChar(start);
				ch[0] = charinfo.character;
				// Log.d("char2", "" + ch[0]);
				if (ch[0] == 8233) {
					return start - num;
				}

				mPaint.getTextWidths(ch, 0, 1, widths);
				widthTotal += Math.ceil(widths[0]);
				if (widthTotal > this.pageWidth) {
					num = 0;
					widthTotal = 0;
					continue;
				}
				start += charinfo.length;
				num += charinfo.length;
			}

		}
		return -1;

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
		}
		return this.getPageStr(local);//mPage.getStrings();
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
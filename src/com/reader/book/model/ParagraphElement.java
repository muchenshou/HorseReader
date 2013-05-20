package com.reader.book.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Paint;

import com.reader.book.Book;
import com.reader.book.CharInfo;
import com.reader.book.Line;
import com.reader.book.bookview.BookView;
import com.reader.config.PageConfig;

public class ParagraphElement extends Element {
	ArrayList<Character> content = new ArrayList<Character>();

	public ParagraphElement(Book book) {
		mBook = book;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Character ch : content) {
			sb.append(ch);
		}
		return sb.toString();
	}

	public void fill() {
		CharInfo ch = mBook.getChar(mElementCursor.getRealFileStart());
		while (ch.character != '\n') {
			content.add(ch.character);
			ch = mBook.getChar(ch.position + ch.length);
		}
		content.add(ch.character);
		mElementCursor.setRealFileLast(ch.position + ch.length - 1);

	}

	public List<Line> toLines() {
		final int width = BookView.Instance.getHeight();
		final Paint paint = PageConfig.pagePaintFromConfig(false);
		List<Line> mLines = new ArrayList<Line>();
		char[] linechar = new char[content.size()];
		float[] widths = new float[content.size()];
		paint.getTextWidths(new String(linechar), widths);
		for (int i = 0; i < linechar.length; i++) {
			linechar[i] = content.get(i);
		}
		mLines.clear();
		float linewidth = 0;
		Line line = null;
		for (int i = 0; i < content.size(); i++) {
			if (linewidth == 0f) {
				line = new Line();
				linewidth += widths[i];
				line.strLine.append(linechar[i]);
				continue;
			}

			if (linewidth + widths[i] > width) {
				linewidth += widths[i];
				line.strLine.append(linechar[i]);
			} else {
				linewidth = 0f;
				mLines.add(line);
			}
		}
		mLines.add(line);
		return mLines;
	}

	@Override
	public int getLength() {
		return content.size();
	}

	// public static Element findByCursor(Book book, Cursor c) {
	// Cursor paraStart;
	// Cursor paraLast;
	// Cursor cursor = new Cursor(c);
	// CharInfo ch = book.getChar(--cursor.mPosition);
	// while (!(cursor.mPosition == 0 || ch.character == '\n')) {
	// cursor.mPosition--;
	// ch = book.getChar(cursor.mPosition);
	// }
	// cursor.mPosition += ch.length;
	// paraStart = cursor;
	//
	// cursor.mPosition = c.mPosition;
	//
	// ch = book.getChar(cursor.mPosition);
	// while (!(cursor.mPosition == book.size() - 1 || ch.character == '\n')) {
	// cursor.mPosition += ch.length;
	// ch = book.getChar(cursor.mPosition);
	// }
	// cursor.mPosition -= ch.length;
	// paraLast = cursor;
	//
	// // judge which type of element
	// // now is text element and may be type image in the future
	// ParagraphElement element = new ParagraphElement(book);
	// element.mRealFileStart = paraStart;
	// element.mRealFileLast = paraLast;
	// element.fill();
	// return element;
	// }
}

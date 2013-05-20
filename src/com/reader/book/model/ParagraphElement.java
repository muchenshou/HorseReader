package com.reader.book.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Paint;
import android.util.Log;

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
		final int width = BookView.Instance.getWidth();
		final Paint paint = PageConfig.pagePaintFromConfig(false);
		List<Line> mLines = new ArrayList<Line>();
		float linewidth = 0;
		Line line = null;
		// if the element only had a char '\n',should return 
		if (content.size() == 1 && content.get(0) == '\n') {
			line = new Line();
			line.element = this;
			mLines.add(line);
			return mLines;
		} 
		char[] linechar = new char[content.size()];
		float[] widths = new float[content.size()];
		for (int i = 0; i < linechar.length; i++) {
			linechar[i] = content.get(i);
		}
		paint.getTextWidths(new String(linechar), widths);
		mLines.clear();
		
		for (int i = 0; i < content.size(); i++) {
			if (linewidth == 0f) {
				line = new Line();
				line.element = this;
				linewidth += widths[i];
				line.strLine.append(linechar[i]);
				continue;
			}

			if (linewidth + widths[i] < width && linechar[i] != '\n') {
				linewidth += widths[i];
				line.strLine.append(linechar[i]);
			} else {
				linewidth = 0f;
				mLines.add(line);
			}
		}
		return mLines;
	}

	@Override
	public int getLength() {
		return content.size();
	}
}

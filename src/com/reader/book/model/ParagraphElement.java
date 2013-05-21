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
	char content[];
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
		if (ch.character == '\n') {
			type = TYPE.NEWLINE;
		}
		StringBuffer buf = new StringBuffer();
		while (ch.character != '\n') {
			buf.append(ch.character);
			ch = mBook.getChar(ch.position + ch.length);
		}
		buf.append(ch.character);
		content = new char[buf.length()];
		buf.getChars(0, buf.length(), content, 0);
		mElementCursor.setRealFileLast(ch.position + ch.length - 1);

	}

	public char charAt(int index) {
		return content[index];
	}

	@Override
	public int getLength() {
		return content.length;
	}
}

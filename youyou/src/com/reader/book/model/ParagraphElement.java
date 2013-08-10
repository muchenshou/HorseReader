package com.reader.book.model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.reader.book.AreaDraw;
import com.reader.book.Book;
import com.reader.book.ParagraphArea;
import com.reader.code.umd.UmdBook;
import com.reader.code.umd.UmdBook.UmdInputStream;


public class ParagraphElement extends MarkupElement {
	public char content[];
	Charset charset;

	public ParagraphElement(Book book, Charset charset) {
		mBook = book;
		this.charset = charset;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Character ch : content) {
			sb.append(ch);
		}
		return sb.toString();
	}

	public void copy(char dest[], int off, int len) {
		System.arraycopy(content, off, dest, 0, len);
	}

	@Override
	public void fill() {
		if (charset.displayName().equals("GBK")) {
			fillByGbk();
		}
		if (charset.displayName().equals("UTF-16")) {
			fillByUnicode();
		}
	}

	private void fillByUnicode() {
		try {
			BufferedInputStream input = new BufferedInputStream(
					new FileInputStream(mBook.bookFile));
			List<Character> chars = new ArrayList<Character>();
			input.read();
			input.read();
			int ch;
			while ((ch = input.read()) != -1) {
				if (ch < 0xD800 || ch > 0xDFFF) {
					int a = input.read();
					a = a << 8;
					ch = a | ch;
				} else {
					byte word[] = new byte[4];
					word[0] = (byte) ch;
					word[1] = (byte) input.read();
					word[2] = (byte) input.read();
					word[3] = (byte) input.read();
					ByteBuffer ww = ByteBuffer.wrap(word);
					ch = (char) charset.decode(ww).get();
				}
				if (ch != 0x20 && ch != 0xA && ch != 0xFDFF)
					chars.add((char) ch);
			}

			this.content = new char[chars.size()];
			for (int i = 0; i < chars.size(); i++) {
				content[i] = chars.get(i);
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void fillByGbk() {
		try {
			BufferedInputStream input = new BufferedInputStream(
					new FileInputStream(mBook.bookFile));
			byte bytes[] = new byte[this.getElementCursor().getLength() + 1];
			List<Character> chars = new ArrayList<Character>();
			byte word[] = new byte[2];
			input.skip(this.getElementCursor().mRealFileStart);
			input.read(bytes);
			int ch;
			for (int i = 0; i < bytes.length - 1;) {
				ch = bytes[i++];
				if (ch >= 0) {
					chars.add((char) ch);
					continue;
				}
				word[0] = (byte) ch;
				word[1] = bytes[i++];
				chars.add(charset.decode(ByteBuffer.wrap(word)).charAt(0));
			}

			this.content = new char[chars.size()];
			for (int i = 0; i < chars.size(); i++) {
				content[i] = chars.get(i);
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public char charAt(int index) {
		return content[index];
	}

	@Override
	public int getLength() {
		return content.length;
	}

	@Override
	AreaDraw toDrawArea() {
		ParagraphArea area = new ParagraphArea(this);
		area.fill();
		return area;
	}

}

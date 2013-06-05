package com.reader.book.model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.reader.book.Book;
import com.reader.book.umd.UmdBook;
import com.reader.book.umd.UmdBook.UmdInputStream;

public class UmdParagraphElement extends Element {
	char content[];
	public UmdParagraphElement(Book book) {
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

	public void copy(char dest[], int off, int len) {
		System.arraycopy(content, off, dest, 0, len);
	}
	@Override
	public void fill() {
		try {
			UmdInputStream umdinput = ((UmdBook)mBook). new UmdInputStream();
			BufferedInputStream input = new BufferedInputStream(umdinput);
			byte bytes[] = new byte[this.getElementCursor().getLength()];
			List<Character> chars = new ArrayList<Character>();
			byte word[] = new byte[2];
			input.skip(this.getElementCursor().mRealFileStart);
			input.read(bytes);
			int ch;
			for(int i=0; i<bytes.length; ) {
				ch = bytes[i++];
				word[0] = (byte)ch;
				word[1] = (byte)bytes[i++];
				ByteBuffer buf = ByteBuffer.wrap(word);
				buf.order(ByteOrder.LITTLE_ENDIAN);
				char c = buf.getChar();
				chars.add(c);
			}
				
			
			this.content = new char[chars.size()];
			for (int i=0; i<chars.size(); i++) {
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
}

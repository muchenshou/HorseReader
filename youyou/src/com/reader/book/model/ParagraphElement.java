package com.reader.book.model;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.reader.book.Book;

public class ParagraphElement extends MarkupElement {
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

	public void copy(char dest[], int off, int len) {
		System.arraycopy(content, off, dest, 0, len);
	}
	@Override
	public void fill() {
		try {
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(mBook.bookFile));
			Charset charset = Charset.forName("gbk");
			byte bytes[] = new byte[this.getElementCursor().getLength()];
			List<Character> chars = new ArrayList<Character>();
			byte word[] = new byte[2];
			input.skip(this.getElementCursor().mRealFileStart);
			input.read(bytes);
			int ch;
			for(int i=0; i<bytes.length; i++) {
				ch = bytes[i];
				if (ch >= 0) {
					chars.add((char) ch);
					continue;
				}
				word[0] = (byte)ch;
				word[1] = bytes[++i];
				chars.add(charset.decode(ByteBuffer.wrap(word)).charAt(0));
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

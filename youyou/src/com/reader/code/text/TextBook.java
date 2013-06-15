/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.code.text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

import com.reader.book.Book;
import com.reader.book.model.MarkupElement;
import com.reader.book.model.ParagraphElement;

public class TextBook extends Book {
	private RandomAccessFile mFile;
	private static final int GBK = 0;
	private static final int UTF8 = 1;
	private int mTextCode = 1;
	int flag = 0xe000;

	public TextBook(File f) {
		bookFile = f;
	}

	private int getTextCode() {
		return GBK;
	}

	@Override
	public void closeBook() {
		try {
			mFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void excuteCmd(int cmd) {

	}

	@Override
	public int getContent(int start, ByteBuffer buffer) {
		int readlen = 0;
		try {
			mFile.seek(start);
		} catch (IOException e1) {
			e1.printStackTrace();
			EOFBOOK = true;
			return -1;
		}
		buffer.clear();
		try {
			for (; readlen < buffer.capacity(); readlen++)
				buffer.put(mFile.readByte());
		} catch (IOException e) {
			e.printStackTrace();
			EOFBOOK = true;
		}
		return readlen;
	}

	@Override
	public void openBook() {
		try {
			mFile = new RandomAccessFile(bookFile, "r");
			mTextCode = this.getTextCode();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public int size() {
		return (int) bookFile.length();
	}

	@Override
	public void pushIntoList(BlockingQueue<MarkupElement> elements) {
		try {
			InputStream input = new BufferedInputStream(new FileInputStream(
					this.bookFile));
			// Charset charset = Charset.forName("gbk");
			MarkupElement element = new ParagraphElement(this);
			int read = 0;
			long size = bookFile.length();
			int ch = 0;

			element.getElementCursor().setRealFileStart(read);
			while ((ch = input.read()) != -1) {
				read++;
				if (ch != 13) {
					if (element == null) {
						element = new ParagraphElement(this);
						element.getElementCursor().setRealFileStart(read - 1);
					}
					continue;
				}
				if (element != null) {
					element.getElementCursor().setRealFileLast(read - 2);
					elements.add(element);
				}
				element = null;
				ch = input.read(); // ch should be equal to 10 here
				read++;

			}
			if (element != null) {
				element.getElementCursor().setRealFileLast((int) size - 1);
				elements.add(element);
			}
			input.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isEof() {
		return EOFBOOK;
	}

}

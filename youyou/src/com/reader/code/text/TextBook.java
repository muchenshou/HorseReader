/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.code.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import android.util.Log;

import com.reader.book.AreaDraw;
import com.reader.book.Book;
import com.reader.book.Page;
import com.reader.book.model.MarkupElement;
import com.reader.book.model.ParagraphElement;
import com.reader.util.InputStreamWithCount;

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

	Charset detectCharset() throws Exception {
		InputStream in = new FileInputStream(this.bookFile);
		InputStreamWithCount input = new InputStreamWithCount(in);
		Charset charset = Charset.forName("gbk");
		int ch = 0;
		ch = input.read();
		if (ch == 0xFF) {
			ch = input.read();
			if (ch == 0xFE)
				charset = Charset.forName("Unicode");
		}
		if (ch == 0xEF) {
			ch = input.read();
			int ch1 = input.read();
			if (ch == 0xBB && ch1 == 0xBF)
				charset = Charset.forName("UTF-8");
		}
		input.close();
		return charset;
	}

	@Override
	public void pushIntoList(BlockingQueue<MarkupElement> elements,
			CopyOnWriteArrayList<Page> pages, LinkedList<AreaDraw> lines) {
		try {
			InputStream in = new FileInputStream(this.bookFile);
			InputStreamWithCount input = new InputStreamWithCount(in);
			Charset charset = Charset.forName("gbk");

			int read = 0;
			long size = bookFile.length();
			int ch = 0;
			charset = detectCharset();
			Log.i("hello", charset.displayName());
			input.setCharset(charset);
			if (charset.displayName().equals("UTF-16")) {
				InputStreamReader reader = new InputStreamReader(input, charset);

				MarkupElement element = new ParagraphElement(this, charset);
				element.getElementCursor().setRealFileStart(read);
				reader.read();
				while ((ch = reader.read()) != -1) {
					int a = ch & 0xff;
					a = a << 8;
					int b = ch & 0xff00;
					b = b >>> 8;
					ch = a | b;
					if (ch != 13) {
						if (element == null) {
							element = new ParagraphElement(this, charset);
							Log.i("hello", "" + (input.getCount() - 2));
							element.getElementCursor().setRealFileStart(
									read - 2);
						}
						continue;
					}
					Log.i("hello", "huanhang:" + input.getCount());
					if (element != null) {
						element.getElementCursor().setRealFileLast(read - 2);
						elements.add(element);
					}
					element = null;
					// ch = input.read(); // ch should be equal to 10 here
					// read+=2;
				}
				if (element != null) {
					element.getElementCursor().setRealFileLast((int) size - 1);
					elements.add(element);
				}
				reader.close();
			} else {
				MarkupElement element = new ParagraphElement(this, charset);
				element.getElementCursor().setRealFileStart(read);
				while ((ch = input.readChar()) != -1) {
					// Log.i("hello",""+(char)ch+""+input.getCount());
					// Log.i("hello",""+ch);
					if (ch != 13) {
						if (element == null) {
							element = new ParagraphElement(this, charset);
							element.getElementCursor()
									.setRealFileStart(
											(int) input.getCount()
													- (ch > 127 ? 2 : 1));
						}
						continue;
					}
					if (element != null) {
						element.getElementCursor().setRealFileLast(
								(int) input.getCount() - 2);
						elements.add(element);
						element.pushIntoLines(lines, pages);
					}
					element = null;
				}
				if (element != null) {
					element.getElementCursor().setRealFileLast((int) size - 1);
					elements.add(element);
					element.pushIntoLines(lines, pages);
				}

			}
			pages.add(Page.ENDPAGE);
			input.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isEof() {
		return EOFBOOK;
	}

}

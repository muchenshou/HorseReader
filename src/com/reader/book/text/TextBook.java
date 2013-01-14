/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import android.util.Log;

import com.reader.book.Book;
import com.reader.book.BookBuffer;
import com.reader.book.CharInfo;

public class TextBook extends Book {
	private RandomAccessFile mFile;
	private BookBuffer bookBuffer = new BookBuffer(this);
	private static final int GBK = 0;
	private static final int UTF8 = 1;
	private int mTextCode = 1;
	int flag = 0xe000;

	public TextBook(File f) {
		bookFile = f;
	}

	private int getTextCode() {
		ByteBuffer bb = ByteBuffer.allocate(400);
		bb.clear();
		this.getContent(0, bb);
		bb.flip();
		int utf = 0;
		for (int i = 0; i < bb.limit() && i < 400; i++) {
			byte b = bb.get(i);
			if ((b & 0xff) >= 0xe4 && (b & 0xff) <= 0xe8) {
				b = bb.get(i + 1);
				if ((b & 0xff) >= 0x80 && (b & 0xff) <= 0xbf) {
					b = bb.get(i + 2);
					if ((b & 0xff) >= 0x80 && (b & 0xff) <= 0xbf) {
						utf++;
						if (utf > 3)
							return UTF8;
					}
				}
			}
		}
		if (utf > 0)
			return UTF8;
		return GBK;
	}

	public void closeBook() {
		try {
			mFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void excuteCmd(int cmd) {

	}

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

	public void openBook() {
		try {
			Log.i("openbook", "openbook");
			mFile = new RandomAccessFile(bookFile, "r");
			mTextCode = this.getTextCode();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public int size() {
		return (int) bookFile.length();
	}

	public CharInfo getChar(int pos) {
		if (mTextCode == GBK) {
			return getCharGbk(pos);
		}
		if (mTextCode == UTF8) {
			return getCharUtf(pos);
		}
		return null;
	}

	public CharInfo getCharGbk(int pos) {
		if (pos >= this.size())
			return null;
		CharInfo charinfo = new CharInfo();
		byte bytes[] = new byte[2];
		if ((int) this.bookBuffer.getByte(pos) >= 0) {
			charinfo.character = (char) this.bookBuffer.getByte(pos);
			charinfo.length = 1;
		}
		if (this.bookBuffer.getByte(pos) == 13
				&& this.bookBuffer.getByte(pos + 1) == 10) {
			charinfo.character = '\n';
			charinfo.length = 2;
		}

		if ((int) this.bookBuffer.getByte(pos) < 0) {
			bytes[0] = this.bookBuffer.getByte(pos);
			bytes[1] = this.bookBuffer.getByte(pos + 1);
			try {
				String str = new String(bytes, "gbk");
				charinfo.character = str.charAt(0);
				charinfo.length = 2;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return charinfo;
	}

	public CharInfo getCharUtf(int pos) {
		if (pos >= this.size())
			return null;
		CharInfo charinfo = new CharInfo();
		byte bytes[] = new byte[3];
		if ((int) this.bookBuffer.getByte(pos) >= 0) {
			charinfo.character = (char) this.bookBuffer.getByte(pos);
			charinfo.length = 1;
		}
		if (this.bookBuffer.getByte(pos) == 13
				&& this.bookBuffer.getByte(pos + 1) == 10) {
			charinfo.character = '\n';
			charinfo.length = 2;
		}

		if ((int) this.bookBuffer.getByte(pos) < 0) {
			bytes[0] = this.bookBuffer.getByte(pos);
			bytes[1] = this.bookBuffer.getByte(pos + 1);
			bytes[2] = this.bookBuffer.getByte(pos + 2);
			try {
				String str = new String(bytes, "utf8");
				charinfo.character = str.charAt(0);
				charinfo.length = 3;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return charinfo;
	}

	public CharInfo getPreChar(int start) {
		if (mTextCode == GBK) {
			return getPreCharGbk(start);
		}
		if (mTextCode == UTF8) {
			return getPreCharUtf(start);
		}
		return null;
	}

	public CharInfo getPreCharGbk(int start) {
		if (this.bookBuffer.getByte(start - 1) == 10
				&& this.bookBuffer.getByte(start - 2) == 13) {
			return getChar(start - 2);
		}
		if (this.bookBuffer.getByte(start - 1) >= 0)
			return getChar(start - 1);
		return getChar(start - 2);
	}

	public CharInfo getPreCharUtf(int start) {
		if (this.bookBuffer.getByte(start - 1) == 10
				&& this.bookBuffer.getByte(start - 2) == 13) {
			return getChar(start - 2);
		}
		if (this.bookBuffer.getByte(start - 1) >= 0)
			return getChar(start - 1);
		return getChar(start - 3);
	}

	public boolean isEof() {
		return EOFBOOK;
	}

}

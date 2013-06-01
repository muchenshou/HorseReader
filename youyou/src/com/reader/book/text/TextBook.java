/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

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
		charinfo.position = pos;
		byte bytes[] = new byte[2];
		if (this.bookBuffer.getByte(pos) >= 0) {
			charinfo.character = (char) this.bookBuffer.getByte(pos);
			charinfo.length = 1;
		}
		if (this.bookBuffer.getByte(pos) == 13
				&& this.bookBuffer.getByte(pos + 1) == 10) {
			charinfo.character = '\n';
			charinfo.length = 2;
		}

		if (this.bookBuffer.getByte(pos) < 0) {
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
		charinfo.position = pos;
		byte bytes[] = new byte[3];
		if (this.bookBuffer.getByte(pos) >= 0) {
			charinfo.character = (char) this.bookBuffer.getByte(pos);
			charinfo.length = 1;
		}
		if (this.bookBuffer.getByte(pos) == 13
				&& this.bookBuffer.getByte(pos + 1) == 10) {
			charinfo.character = '\n';
			charinfo.length = 2;
		}

		if (this.bookBuffer.getByte(pos) < 0) {
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

	@Override
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
	
	public Map<Integer,CharInfo> loadContent() {
		try {
			InputStream input = new BufferedInputStream(new FileInputStream(this.bookFile));
			Map<Integer, CharInfo> elements = new HashMap<Integer, CharInfo>();
			final long size = bookFile.length();
			long read = 0;
			byte bytes[] = new byte[2];
			Charset charset = Charset.forName("gbk");
			
			while (read < size) {
				CharInfo charinfo = new CharInfo();
				int ch = input.read();
				read++;
				if (ch <= 127) {
					charinfo.length = 1;
					charinfo.character = (char) ch;
					charinfo.position = (int) read - 1;
					elements.put(charinfo.position, charinfo);
					continue;
				}
				bytes[0] = (byte)ch;
				bytes[1] = (byte)input.read();
				read++;
				charinfo.length = 2;
				charinfo.character = charset.decode(ByteBuffer.wrap(bytes)).charAt(0);
				charinfo.position = (int) read - 1;
				elements.put(charinfo.position, charinfo);
			}
			input.close();
			return elements;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public boolean isEof() {
		return EOFBOOK;
	}

}

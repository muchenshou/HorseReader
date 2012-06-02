package com.Reader.Book.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.Reader.Book.Book;
import com.Reader.Book.BookBuffer;
import com.Reader.Book.CharInfo;

public class TextBook extends Book {
	private RandomAccessFile mFile;
	private BookBuffer bookBuffer = new BookBuffer(this);
	public TextBook(File f) {
		bookFile = f;
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
			mFile = new RandomAccessFile(bookFile, "r");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public int size() {
		return (int) bookFile.length();
	}

	public CharInfo getChar(int pos) {
		//
		//Log.i("gbktext","here1");
		if (pos >= this.size())
			return null;
		CharInfo charinfo = new CharInfo();
		byte bytes[] = new byte[2];
		if ((int)this.bookBuffer.getByte(pos) >= 0) {
			charinfo.character = (char)this.bookBuffer.getByte(pos);
			charinfo.length = 1;
		}
		if (this.bookBuffer.getByte(pos) == 13 &&
				this.bookBuffer.getByte(pos+1) == 10){
			charinfo.character = '\n';
			//Log.i("gbktext", "fanxiegang"+(int)charinfo.character);
			charinfo.length = 2;
		}
			
		if ((int)this.bookBuffer.getByte(pos) < 0) {
			bytes[0] = this.bookBuffer.getByte(pos);
			bytes[1] = this.bookBuffer.getByte(pos+1);
			try {
				String str = new String(bytes,"gbk");
				charinfo.character = str.charAt(0);
				charinfo.length = 2;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
		}
		
		//charinfo.position = pos;
		return charinfo;
	}

	public CharInfo getPreChar(int start) {
		//
		return getChar(start - 2);
	}

	public boolean isEof() {
		// TODO Auto-generated method stub
		return EOFBOOK;
	}

}

/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book;

import java.io.File;
import java.nio.ByteBuffer;

import com.reader.book.manager.BookPosition;

public abstract class Book {
	protected boolean EOFBOOK = false;
	public BookPosition openOffset = new BookPosition(0,0,0);
	protected File bookFile;

	public abstract void openBook();

	public abstract void closeBook();

	public abstract void excuteCmd(int cmd);

	public abstract int getContent(int start, ByteBuffer buffer);

	public abstract int size();

	public boolean isEof() {
		return EOFBOOK;
	}

	public abstract CharInfo getChar(int start);

	public abstract CharInfo getPreChar(int mStart);

	public String getName() {
		String name = bookFile.getName();
		return name.substring(0, name.length() - 4);
	}
}

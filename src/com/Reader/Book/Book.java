package com.Reader.Book;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface Book {
	public void openBook();

	public void closeBook();

	public void excuteCmd(int cmd);

	public int getContent(int start, ByteBuffer buffer) throws IOException;
	public int size();

	public CharInfo getChar(int start);
	public CharInfo getPreChar(int mStart);
}

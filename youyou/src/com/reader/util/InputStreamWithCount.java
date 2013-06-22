package com.reader.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class InputStreamWithCount extends InputStream {
	long count = 0;
	InputStream inputStream;
	private Charset charset;
	private int typeCharset = gbk;
	private static int gbk = 0;
	private static int utf8 = 1;
	private static int utf16 = 2;

	public long getCount() {
		return count;
	}

	public InputStreamWithCount(InputStream in) {
		inputStream = in;
	}

	@Override
	public int read() throws IOException {
		int c = inputStream.read();
		if (c != -1)
			count++;
		return c;
	}
	public void setCharset(Charset charset) {
		this.charset = charset;
		if (charset.displayName().equals("UTF-16")) {
			typeCharset = utf16;
		} if (charset.displayName().equals("gbk")) {
			typeCharset = gbk;
		} if (charset.displayName().equals("UTF-8")){
			typeCharset = utf8;
		}
	}
	public int readChar() throws IOException {
		int ch;
		if (typeCharset == gbk) {
			if ((ch = read()) != -1) {
				if (ch >= 0 && ch <= 127) {
					if (ch == 13)
						read();
					return ch;
				}else {
					byte word[] = new byte[2];
					word[0] = (byte)ch;
					word[1] = (byte)read();
					ByteBuffer bufWord = ByteBuffer.wrap(word);
					bufWord.order(ByteOrder.LITTLE_ENDIAN);
					return charset.decode(bufWord).get();
				}
			}
		}
		if (typeCharset == utf8) {

		}
		if (typeCharset == utf16) {

		}
		return -1;
	}

	@Override
	public int available() throws IOException {
		return inputStream.available();
	}

	@Override
	public void close() throws IOException {
		inputStream.close();
	}

	@Override
	public void mark(int readlimit) {
		inputStream.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return inputStream.markSupported();
	}

	@Override
	public synchronized void reset() throws IOException {
		inputStream.reset();
	}

	@Override
	public long skip(long byteCount) throws IOException {
		long c = inputStream.skip(byteCount);
		if (c > 0)
			count += c;
		return c;
	}

}

package com.reader.util;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamWithCount extends InputStream {
	long count = 0;
	InputStream inputStream;

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
	public int read(byte[] buffer, int offset, int length) throws IOException {
		int c = inputStream.read(buffer, offset, length);
		if (c != -1)
			count += c;
		return c;
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		int c = inputStream.read(buffer);
		if (c != -1)
			count += c;
		return c;
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

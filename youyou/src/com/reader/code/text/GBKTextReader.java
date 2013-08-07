package com.reader.code.text;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import com.reader.book.model.MarkupElement;
import com.reader.book.model.ParagraphElement;

public class GBKTextReader {
	private InputStream _in;
	private int _pos;

	public GBKTextReader(InputStream in, int pos) {
		_in = in;
		_pos = pos;
	}

	public GBKTextReader(InputStream in) {
		this(in, 0);
	}

	public MarkupElement next() {
		byte chBuf[];
		Charset set = Charset.forName("GBK");
		ParagraphElement ele = new ParagraphElement(null, set);
		StringBuffer sbuf = new StringBuffer();
		try {
			ele.mElementCursor.mRealFileStart = _pos;
			int b = read();
			if (b >= 127) {
				chBuf = new byte[2];
				chBuf[0] = (byte) b;
				chBuf[1] = (byte) read();
				ByteBuffer bbuf = ByteBuffer.wrap(chBuf);
				bbuf.order(ByteOrder.LITTLE_ENDIAN);
				sbuf.append(set.decode(bbuf).get());
				ele.content = sbuf.toString().toCharArray();
				ele.mElementCursor.len = 2;
				return ele;
			}
			sbuf.append((char) b);
			ele.content = sbuf.toString().toCharArray();
			ele.mElementCursor.len = 1;
			return ele;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private int read() throws IOException {
		_pos++;
		return _in.read();
	}

	public boolean hasNext() {
		try {
			return _in.available() != 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}

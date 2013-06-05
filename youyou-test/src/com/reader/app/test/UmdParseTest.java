package com.reader.app.test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import com.reader.book.model.Element;
import com.reader.book.umd.UmdBook;
import com.reader.book.umd.UmdInfo;
import com.reader.book.umd.UmdInfo.Chapter;
import com.reader.book.umd.UmdParse;

public class UmdParseTest extends AndroidTestCase {
	// public void testUmd() {
	// try {
	// Log.i("hello", "testUmda");
	//
	// UmdInfo umdinfo = new UmdParse(new File(Environment
	// .getExternalStorageDirectory().getPath()
	// + "/newmbook/微信 简单之美.umd"), "r").parseBook();
	// Iterator<Chapter> iter = umdinfo.chapterIter();
	// while (iter.hasNext()) {
	// Log.i("hello", iter.next().toString());
	// }
	// UmdBook book = new UmdBook(new File(Environment
	// .getExternalStorageDirectory().getPath()
	// + "/newmbook/微信 简单之美.umd"));
	// Log.i("hello",""+book.getChar(0).character);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	public void testUmdInflater() {
		try {
			UmdBook umd = new UmdBook(new File(Environment
					.getExternalStorageDirectory().getPath()
					+ "/newmbook/微信 简单之美.umd"));
			// ByteBuffer buf =
			// ByteBuffer.wrap(umd.umdinflate.getContentBlock(0, 0, 100));

			ByteBuffer buf = ByteBuffer.wrap(umd.umdinflate.getContentBlock(0,
					0, 100));
			ByteOrder order = ByteOrder.LITTLE_ENDIAN;
			buf.order(order);
			StringBuffer sb = new StringBuffer();
			while (buf.position() < buf.limit()) {
				sb.append(buf.getChar());
			}
			Log.i("hello", "" + sb);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

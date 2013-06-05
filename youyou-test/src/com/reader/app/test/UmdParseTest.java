package com.reader.app.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import com.reader.book.model.Element;
import com.reader.book.umd.UmdBook;
import com.reader.book.umd.UmdBook.UmdInputStream;
import com.reader.book.umd.UmdInfo;
import com.reader.book.umd.UmdInfo.Chapter;
import com.reader.book.umd.UmdParse;

public class UmdParseTest extends AndroidTestCase {
	public void testUmd() {
		try {
			Log.i("hello", "testUmda");

			UmdBook book = new UmdBook(new File(Environment
					.getExternalStorageDirectory().getPath()
					+ "/newmbook/微信 简单之美.umd"));
			BlockingQueue<Element> elements = new LinkedBlockingQueue<Element>();
			book.pushIntoList(elements);
			try {
				File o = new File("/sdcard/Books/da.txt");
				o.delete();
				o.createNewFile();
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(o));
				for (Element e : elements) {
					e.fill();
					Log.i("hello", "start:"
							+ e.getElementCursor().getRealFileStart() + "end:"
							+ e.getElementCursor().getRealFileLast());
					Log.i("hello", "d:" + e.toString());

					out.write(e.toString().getBytes("gbk"));
					out.write(13);
					out.write(10);
					out.write(13);
					out.write(10);

				}
				out.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	public void testUmdInflater() {
//		try {
//			UmdBook umd = new UmdBook(new File(Environment
//					.getExternalStorageDirectory().getPath()
//					+ "/newmbook/微信 简单之美.umd"));
//
//			ByteBuffer buf = ByteBuffer.allocate(65520);
//			umd.getContent(0, buf);
//			buf.flip();
//			buf.order(ByteOrder.LITTLE_ENDIAN);
//			StringBuffer sb = new StringBuffer();
//			while (buf.position() < buf.limit() - 1) {
//				sb.append(buf.getChar());
//				if (sb.length() == 20) {
//					Log.i("hello", sb.toString());
//					sb = new StringBuffer();
//				}
//			}
//			Log.i("hello", "" + sb);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	public void testUmdInputStream(){
//		try {
//			UmdBook umd = new UmdBook(new File(Environment
//					.getExternalStorageDirectory().getPath()
//					+ "/newmbook/微信 简单之美.umd"));
//			UmdInputStream umdinput = umd. new UmdInputStream();
//			byte[] bytes = new byte[2];
//			bytes[0] = (byte)umdinput.read();
//			bytes[1] = (byte)umdinput.read();
//			ByteBuffer buf = ByteBuffer.wrap(bytes);
//			buf.order(ByteOrder.LITTLE_ENDIAN);
//			Log.i("hello", "" + buf.getChar());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}

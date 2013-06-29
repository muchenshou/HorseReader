package com.reader.app.test;

import android.test.AndroidTestCase;

public class UmdParseTest extends AndroidTestCase {

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

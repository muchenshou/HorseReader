package com.reader.app.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.test.AndroidTestCase;
import android.util.Log;

import com.reader.book.model.Element;
import com.reader.book.text.TextBook;

public class TextBookTest extends AndroidTestCase {
	public void testTextLoad() {
		TextBook book = new TextBook(new File("/sdcard/Books/suan.txt"));
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
//				Log.i("hello", "start:"
//						+ e.getElementCursor().getRealFileStart() + "end:"
//						+ e.getElementCursor().getRealFileLast());
				Log.i("hello", "d:" + e.toString());

				out.write(e.toString().getBytes("gbk"));
				out.write(13);
				out.write(10);out.write(13);
				out.write(10);

			}
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		Map<Integer, CharInfo> map = book.loadContent();
//		Set<Entry<Integer, CharInfo>> entry = map.entrySet();
//		try {
//			Log.i("hello","here");
//			File o = new File("/sdcard/Books/da.txt");
//			o.createNewFile();
//			BufferedOutputStream out = new BufferedOutputStream(
//					new FileOutputStream(o));
//			Charset cs = Charset.forName("gbk");
//			ArrayList<Integer> list = new ArrayList<Integer>();
//			for (Entry<Integer, CharInfo> e : entry) {
//				list.add(e.getKey());
//			}
//			Collections.sort(list);
//			for (Integer i:list) {
//				CharBuffer b = CharBuffer.allocate(1);
//				b.put(map.get(i).character);
//				b.flip();
//				out.write(cs.encode(b).array());
//			}
//			out.close();
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
	}
}

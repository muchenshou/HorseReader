package com.reader.app.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import android.test.AndroidTestCase;
import android.util.Log;

import com.reader.book.CharInfo;
import com.reader.book.text.TextBook;

public class TextBookTest extends AndroidTestCase {
	public void testTextLoad() {
		TextBook book = new TextBook(new File("/sdcard/Books/suan.txt"));
		Map<Integer, CharInfo> map = book.loadContent();
		Set<Entry<Integer, CharInfo>> entry = map.entrySet();
		try {
			Log.i("hello","here");
			File o = new File("/sdcard/Books/da.txt");
			o.createNewFile();
			BufferedOutputStream out = new BufferedOutputStream(
					new FileOutputStream(o));
			Charset cs = Charset.forName("gbk");
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (Entry<Integer, CharInfo> e : entry) {
				list.add(e.getKey());
			}
			Collections.sort(list);
			for (Integer i:list) {
				CharBuffer b = CharBuffer.allocate(1);
				b.put(map.get(i).character);
				b.flip();
				out.write(cs.encode(b).array());
			}
			out.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}

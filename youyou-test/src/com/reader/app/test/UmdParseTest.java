package com.reader.app.test;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import com.reader.book.umd.UmdInfo;
import com.reader.book.umd.UmdInfo.Chapter;
import com.reader.book.umd.UmdParse;

public class UmdParseTest extends AndroidTestCase {
	public void testUmd() {
		try {
			Log.i("hello", "testUmda");

			UmdInfo umdinfo = new UmdParse(new File(Environment
					.getExternalStorageDirectory().getPath()
					+ "/newmbook/微信 简单之美.umd"), "r").parseBook();
			Iterator<Chapter> iter = umdinfo.chapterIter();
			while (iter.hasNext()) {
				Log.i("hello", iter.next().toString());
			}
			// Element.Iterator im = book.iterator(0, 0);
			// while (im.hasNext()) {
			// Log.i("hello", im.next().toString());
			// }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

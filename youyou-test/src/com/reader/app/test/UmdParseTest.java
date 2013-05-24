package com.reader.app.test;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.util.Log;

import com.reader.book.model.BookModel;
import com.reader.book.model.Element;
import com.reader.book.umd.UmdBook;

public class UmdParseTest extends AndroidTestCase {
	public void testUmd() {
		try {
			BookModel book = new BookModel(new UmdBook(new File(Environment
					.getExternalStorageDirectory().getPath()
					+ "/newmbook/微信 简单之美.umd")));
			Log.i("hello","testUmd");
			book.hashCode();
			Element.Iterator im = book.iterator(0, 0);
			while (im.hasNext()) {
				Log.i("hello", im.next().toString());
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package com.reader.app.test;

import java.io.File;

import com.reader.code.epub.Epub;

import android.os.Environment;
import android.test.AndroidTestCase;

public class EpubBook extends AndroidTestCase {
	public void testEpub() {
		Epub epub = new Epub(new File(Environment.getExternalStorageDirectory()
				.getPath() + "/dd.epub"));
		epub.openBook();
	}
}

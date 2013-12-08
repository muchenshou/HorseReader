/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.reader.document.epub.EpubDocument;
import com.reader.document.epub.EpubPageProvider;
import com.reader.document.txt.TxtPageProvider;
import com.reader.view.GLView;

public class ReadingActivity extends Activity {
	public GLView bookView;
	private String mBookName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		String bookName = getIntent().getStringExtra("bookname");
		// ///// test
		try {
			File my = File.createTempFile("aaaa", ".txt");
			my.canWrite();
			my.canRead();
			OutputStream o = new FileOutputStream(my);
			InputStream in = getResources().getAssets().open("zhetian.epub");
			byte[] filedata = new byte[1024];
			int num;
			while ((num = in.read(filedata)) > 0) {
				o.write(filedata, 0, num);
			}
			o.close();
			bookName = my.getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.mBookName = bookName;
		// test txt
		// TxtPageProvider provider = new TxtPageProvider(this, bookName);
		// provider.loadDocument();
		// test epub
		EpubPageProvider provider = new EpubPageProvider(this, bookName);
		 provider.loadDocument();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// 必须创建一项
		return super.onCreateOptionsMenu(menu);
	}

	public static int TURN_SETTING = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(this);
		String str = spf.getString("turn_page", "none");
		if (str.equals("none")) {
			// bookView.setTurnAnimation(new NoTurnAnimation(this));
		}
		if (str.equals("real")) {
			// bookView.setTurnAnimation(new SimulateTurnPage(this));
		}
	}

}
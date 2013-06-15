/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.app;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.reader.book.manager.BookManager;
import com.reader.book.manager.BookPosition;
import com.reader.preference.ReadingSetting;
import com.reader.record.BookHistory;
import com.reader.view.BookView;
import com.reader.view.GLView;
import com.reader.view.curl.NoTurnAnimation;
import com.reader.view.curl.SimulateTurnPage;

public class ReadingActivity extends Activity {
	public GLView bookView;
	public BookManager bookmanager;
	private String mBookName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		String bookName = getIntent().getStringExtra("bookname");
		this.mBookName = bookName;
		BookPosition position = new BookPosition(0, 0, 0);
		BookHistory history = new BookHistory(this);
		if (history.exist(bookName)) {
			position = history.getPosition(bookName);
		} else {
			history.storePosition(bookName, null);
		}

		try {
			if (!new File(bookName).exists()) {
				Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
				return;
			}
			bookmanager = new BookManager(ReadingActivity.this, new File(
					bookName));
			bookView = new GLView(this, bookmanager.openBook(position));
			bookView.setFocusable(true);
			this.bookView.setBgBitmap(BitmapFactory.decodeResource(
					getResources(), R.drawable.bg));
			setLookingBookView();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		BookHistory history = new BookHistory(this);
		history.storePosition(this.mBookName,
				this.bookView.mBookContent.getCurPosition());
		super.onStop();
	}

	private void setLookingBookView() {
		setContentView(bookView);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下键盘上返回按钮
		// if (keyCode == KeyEvent.KEYCODE_BACK) {
		// Intent intent = new Intent();
		// intent.putExtra("BookReading",
		// this.bookView.mBookContent.getCurPosition());
		// setResult(RESULT_OK, intent);
		// finish();
		// }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// 必须创建一项
		return super.onCreateOptionsMenu(menu);
	}

	public static int TURN_SETTING = 1;

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		startActivityForResult(new Intent(this, ReadingSetting.class),
				TURN_SETTING);
		return false;// 返回为true 则显示系统menu
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(this);
		String str = spf.getString("turn_page", "none");
		if (str.equals("none")) {
			bookView.setTurnAnimation(new NoTurnAnimation(this));
		}
		if (str.equals("real")) {
			bookView.setTurnAnimation(new SimulateTurnPage(this));
		}
	}

}
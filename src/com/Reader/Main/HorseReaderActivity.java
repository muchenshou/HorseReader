/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.Reader.Main;

import com.Reader.Main.R;
import com.Reader.Record.BookHistory;
import com.Reader.ui.BookAdapter;
import com.Reader.ui.ShelfAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class HorseReaderActivity extends Activity implements ShelfAdapter.CallBack{
	/** Called when the activity is first created. */
	public static final int FILE_RESULT_CODE = 1;
	public static final int READING_RESULT_CODE = 2;
	public TextView textView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ListView listview = (ListView) findViewById(R.id.selectFile);
		if (listview == null) {
			Log.d("song", "is null");
			finish();
		}
		listview.setDividerHeight(0);
	}

	@Override
	protected void onStart() {
		ListView listview = (ListView) findViewById(R.id.selectFile);
		BookHistory history = new BookHistory(this);
		ShelfAdapter adapter = new ShelfAdapter(this, history.getHistory());
		listview.setAdapter(adapter);
		super.onStart();
	}

	@Override
	public void CallBackOpen(String bookName) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(HorseReaderActivity.this,
				ReadingActivity.class);
		intent.putExtra("bookname", bookName);
		HorseReaderActivity.this.getParent().startActivityForResult(intent,HorseReaderActivity.READING_RESULT_CODE);

	}

	
}


package com.Reader.Main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.Reader.Main.R;
import com.Reader.Record.BookHistory;
import com.Reader.Record.BookInfo;
import com.Reader.ui.BookAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HorseReaderActivity extends Activity {
	/** Called when the activity is first created. */
	public static final int FILE_RESULT_CODE = 1;
	public static final int READING_RESULT_CODE = 2;
	private String mCurrentBook;
	public TextView textView;
	private void setCurrentBook(String book){
		mCurrentBook = null;
		mCurrentBook = book;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ListView listview = (ListView) findViewById(R.id.selectFile);
		if (listview == null) {
			Log.d("song", "is null");
			finish();
		}
		listview.setOnItemClickListener(new ListView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterview, View view,
					int position, long id) {
				if (((String) adapterview.getAdapter().getItem(position))
						.equals("selectfile")) {
					Intent intent = new Intent(HorseReaderActivity.this,
							FileManager.class);
					HorseReaderActivity.this.getParent().startActivityForResult(intent,
							HorseReaderActivity.FILE_RESULT_CODE);
				} else {
					// openbook
					String bookName = adapterview.getAdapter()
							.getItem(position).toString();
					Intent intent = new Intent(HorseReaderActivity.this,
							ReadingActivity.class);
					BookHistory his = new BookHistory(HorseReaderActivity.this);
					intent.putExtra("bookname", bookName);
					intent.putExtra("position", his.getPosition(bookName));
										setCurrentBook(bookName);
					HorseReaderActivity.this.getParent().startActivityForResult(intent,HorseReaderActivity.READING_RESULT_CODE);

				}
			}

		});

	}

	@Override
	protected void onStart() {
		ListView listview = (ListView) findViewById(R.id.selectFile);
		BookHistory history = new BookHistory(this);
		BookAdapter adapter = new BookAdapter(this, history.getHistory());
		listview.setAdapter(adapter);
		super.onStart();
	}

	
}


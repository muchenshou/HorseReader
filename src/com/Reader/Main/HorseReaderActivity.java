package com.Reader.Main;

import com.Reader.Main.R;
import com.Reader.Record.BookHistory;
import com.Reader.ui.BookAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class HorseReaderActivity extends Activity {
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
		listview.setOnItemClickListener(new ListView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterview, View view,
					int position, long id) {
				if (((String) adapterview.getAdapter().getItem(position))
						.equals("selectfile")) {
					;
				} else {
					// openbook
					String bookName = adapterview.getAdapter()
							.getItem(position).toString();
					Intent intent = new Intent(HorseReaderActivity.this,
							ReadingActivity.class);
					intent.putExtra("bookname", bookName);
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


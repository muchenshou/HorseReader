package com.Reader.Main;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.Reader.Main.R;
import com.Reader.Record.RecordBookList;
import com.Reader.Record.RecordHistory;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class FileManager extends ListActivity {

	boolean isBook(File file) {
		// Toast.makeText(this, "isBook", Toast.LENGTH_SHORT).show();
		return file.toString().substring(file.toString().lastIndexOf('.') + 1)
				.toLowerCase().equals("umd")
				|| file.toString().substring(
						file.toString().lastIndexOf('.') + 1).toLowerCase()
						.equals("txt");
	}

	private void searchBookFromDir(List<String> list, File file) {
		File[] array = file.listFiles();
		if (array == null) {
			return;
		}
		for (int i = 0; i < array.length; i++) {
			if (array[i].isDirectory() == true) {
				searchBookFromDir(list, array[i]);
			} else {
				if (isBook(array[i]) == true) {
					list.add(array[i].toString());
				}
			}
		}
	}
	private LinkedList<String> searchBook() {

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			LinkedList<String> list = new LinkedList<String>();
			searchBookFromDir(list, new File("/sdcard/"));
			return list;
		} else {
			Toast.makeText(this, "Œ¥∑¢œ÷SDø®£°", Toast.LENGTH_LONG).show();
			return null;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fileselect);
		Button update = (Button) findViewById(R.id.updateButton);
		update.setClickable(true);
		update.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				RecordBookList record = new RecordBookList();
				record.setRecords(searchBook());
				record.writeFile();
				ListView listview = getListView();
				listview.setAdapter(new BookAdapter(FileManager.this, new File(
						record.getFileName())));
			}
		});
		Button cancel = (Button) findViewById(R.id.cancelButton);
		cancel.setClickable(true);
		cancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
		if (searchBook().size() == 0) {
			Toast.makeText(this, "zero", Toast.LENGTH_SHORT).show();
		}
		RecordBookList record = new RecordBookList();
		record.setRecords(searchBook());
		record.writeFile();
		ListView listview = this.getListView();
		listview.setAdapter(new BookAdapter(this,
				new File(record.getFileName())));
	}

	//
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String bookname = (String) l.getAdapter().getItem(position);
		RecordHistory history = new RecordHistory();

		if (history.isHaveRecord(bookname) != true) {
			history.addFirst(bookname, "" + 0);
		}
		history.writeFile();
		// history
		openFile(new File(bookname));

	}

	public void DisplayToast(String str)

	{
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	private void openFile(File f) {
		Intent intent = new Intent();
		if (isBook(f)) {
			intent.putExtra("umdfile", f.getPath());
			Log.d("openFIle", f.getPath());
			setResult(RESULT_OK, intent);
			finish();
		}

	}
	
}

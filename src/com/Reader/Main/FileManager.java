/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.Reader.Main;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.Reader.Main.R;
import com.Reader.Record.BookInfo;
import com.Reader.Record.BookLibrary;
import com.Reader.Record.BookHistory;
import com.Reader.ui.BookAdapter;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.content.Context;

public class FileManager extends ListActivity implements View.OnClickListener{

	private List<String> searchBook(BookLibrary lib) {

		ProgressDialog progress = new ProgressDialog(this);
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
		// progress.setTitle("提示");//设置标题
		progress.setIcon(R.drawable.icon);// 设置图标
		progress.setMessage("搜索中...");
		progress.setIndeterminate(false);// 设置进度条是否为不明确
		LinkedList<String> list = new LinkedList<String>();
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			Toast.makeText(this, "未发现SD卡！", Toast.LENGTH_LONG).show();
			return list;
		}
		lib.deleteAllBook();
		SearchBook sea = new SearchBook(progress, lib,this);

		progress.show();
		sea.execute("");
		return sea.getList();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fileselect);
		
		Button update = (Button) findViewById(R.id.updateButton);
		update.setClickable(true);
		update.setOnClickListener(this);
		setListViewContent();

	}
	public void setListViewContent(){
		BookLibrary record = new BookLibrary((Context) this);
		List<BookInfo> list = record.readLibrary();
		if (list.size() == 0) {
			Toast.makeText(this, "zero", Toast.LENGTH_SHORT).show();
		}
		ListView listview = this.getListView();
		listview.setAdapter(new BookAdapter(this, list));
	}
	//
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String bookname = (String) l.getAdapter().getItem(position);
		// history
		Log.i("filemanager", bookname);
		openFile(new File(bookname));

	}

	public void DisplayToast(String str)
	{
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	private void openFile(File f) {
		//Intent intent = new Intent();
		if (SearchBook.isBook(f)) {
			//intent.putExtra("bookfile", f.getPath());
			Log.d("openFile", f.getPath());
			//setResult(RESULT_OK, intent);
			Intent intent = new Intent(FileManager.this,
					ReadingActivity.class);
			intent.putExtra("bookname", f.getPath());
			FileManager.this.getParent().startActivityForResult(intent,HorseReaderActivity.READING_RESULT_CODE);
			//finish();
		}

	}

	public void onClick(View v) {
		Log.i("[onclick]", "updatebutton1");
		// TODO Auto-generated method stub
		if (v.getId() == R.id.updateButton){
			Log.i("[onclick]", "updatebutton");
			BookLibrary lib = new BookLibrary(FileManager.this);
			searchBook(lib);
		}
		
	}

}

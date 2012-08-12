package com.Reader.Fragment;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.Reader.Main.R;
import com.Reader.Main.ReadingActivity;
import com.Reader.Main.SearchBook;
import com.Reader.Record.BookInfo;
import com.Reader.Record.BookLibrary;
import com.Reader.Ui.BookAdapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class LocalFileListFragment extends Fragment implements
		View.OnClickListener ,OnItemClickListener{
	ListView mListView;

	@Override	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View tv = inflater.inflate(R.layout.fileselect, container, false);
		this.mListView = (ListView) tv.findViewById(R.id.filelist);
		this.mListView.setOnItemClickListener(this);
		mListView.setScrollingCacheEnabled(false);
		Button update = (Button) tv.findViewById(R.id.updateButton);
		update.setClickable(true);
		update.setOnClickListener(this);
		setListViewContent();
		
		return tv;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private List<String> searchBook(BookLibrary lib) {

		ProgressDialog progress = new ProgressDialog(this.getActivity());
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
		// progress.setTitle("提示");//设置标题
		progress.setIcon(R.drawable.icon);// 设置图标
		progress.setMessage("搜索中...");
		progress.setIndeterminate(false);// 设置进度条是否为不明确
		LinkedList<String> list = new LinkedList<String>();
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			Toast.makeText(this.getActivity(), "未发现SD卡！", Toast.LENGTH_LONG)
					.show();
			return list;
		}
		lib.deleteAllBook();
		SearchBook sea = new SearchBook(progress, lib, this);

		progress.show();
		sea.execute("");
		return sea.getList();

	}

	public void setListViewContent() {
		BookLibrary record = new BookLibrary((Context) this.getActivity());
		List<BookInfo> list = record.readLibrary();
		if (list.size() == 0) {
			Toast.makeText(this.getActivity(), "zero", Toast.LENGTH_SHORT)
					.show();
		}
		ListView listview = mListView;
		listview.setAdapter(new BookAdapter(this.getActivity(), list));
	}

	public void DisplayToast(String str) {
		Toast.makeText(this.getActivity(), str, Toast.LENGTH_SHORT).show();
	}

	private void openFile(File f) {
		// Intent intent = new Intent();
		if (SearchBook.isBook(f)) {
			// intent.putExtra("bookfile", f.getPath());
			Log.d("openFile", f.getPath());
			// setResult(RESULT_OK, intent);
			Intent intent = new Intent(this.getActivity(),
					ReadingActivity.class);
			intent.putExtra("bookname", f.getPath());
			startActivityForResult(intent,0);
			// finish();
		}

	}

	public void onClick(View v) {
		Log.i("[onclick]", "updatebutton1");
		// TODO Auto-generated method stub
		if (v.getId() == R.id.updateButton) {
			Log.i("[onclick]", "updatebutton");
			BookLibrary lib = new BookLibrary(this.getActivity());
			searchBook(lib);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> l, View arg1, int position, long arg3) {
		String bookname = (String) l.getAdapter().getItem(position);
		// history
		Log.i("filemanager", bookname);
		openFile(new File(bookname));
	}

}
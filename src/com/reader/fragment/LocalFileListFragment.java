package com.reader.fragment;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.reader.main.R;
import com.reader.record.BookInfo;
import com.reader.record.BookLibrary;
import com.reader.searchfile.FileListAdapter;
import com.reader.searchfile.SearchFileTask;
import com.reader.ui.BookAdapter;
import com.reader.main.ReadingActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
		View.OnClickListener, OnItemClickListener {
	ListView mListView;
	FileListAdapter mFileListAdapter;
	MyHandler mMyhandler = new MyHandler(Looper.getMainLooper());

	class MyHandler extends Handler {
		public MyHandler(Looper L) {
			super(L);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mFileListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View tv = inflater.inflate(R.layout.fileselect, container, false);
		this.mListView = (ListView) tv.findViewById(R.id.filelist);
		this.mListView.setOnItemClickListener(this);
		mListView.setScrollingCacheEnabled(false);
		mFileListAdapter = new FileListAdapter(getActivity(), mMyhandler);
		mListView.setAdapter(mFileListAdapter);
		Button update = (Button) tv.findViewById(R.id.updateButton);
		update.setClickable(true);
		update.setOnClickListener(this);
		return tv;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void searchBook(BookLibrary lib) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			Toast.makeText(this.getActivity(), "Œ¥∑¢œ÷SDø®£°", Toast.LENGTH_LONG)
					.show();
			return;
		}
		lib.deleteAllBook();
		Log.i("++++++++", "dafdfadfadsf");
		SearchFileTask sft = new SearchFileTask(this.getActivity()
				.getApplicationContext(), mFileListAdapter, lib);
		sft.execute(null);
		return;

	}

	public void DisplayToast(String str) {
		Toast.makeText(this.getActivity(), str, Toast.LENGTH_SHORT).show();
	}

	private void openFile(File f) {
		// Intent intent = new Intent();
		Intent intent = new Intent(this.getActivity(), ReadingActivity.class);
		intent.putExtra("bookname", f.getPath());
		startActivityForResult(intent, 0);

	}

	public void onClick(View v) {
		if (v.getId() == R.id.updateButton) {
			BookLibrary lib = new BookLibrary(this.getActivity());
			searchBook(lib);
		}

	}

	public void onItemClick(AdapterView<?> l, View arg1, int position, long arg3) {
		String bookname = (String) l.getAdapter().getItem(position);
		// history
		openFile(new File(bookname));
	}

}
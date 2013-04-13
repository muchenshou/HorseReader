package com.reader.fragment;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.reader.main.R;
import com.reader.main.ReadingActivity;
import com.reader.record.BookLibrary;
import com.reader.searchfile.FileListAdapter;
import com.reader.searchfile.SearchFile;
import com.reader.searchfile.SearchFile.FindOneBehavior;
import com.reader.searchfile.SearchFileMultiThread;
import com.reader.ui.ActionBar.Action;
import com.reader.util.FilenameExtFilter;

public class LocalFileListFragment extends Fragment implements
		View.OnClickListener, OnItemClickListener , Action{
	ListView mListView;
	FileListAdapter mFileListAdapter;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mFileListAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View tv = inflater.inflate(R.layout.fileselect, container, false);
		this.mListView = (ListView) tv.findViewById(R.id.filelist);
		this.mListView.setOnItemClickListener(this);
		mListView.setScrollingCacheEnabled(false);
		mFileListAdapter = new FileListAdapter(getActivity());
		mListView.setAdapter(mFileListAdapter);
		return tv;
	}

	private void searchBook(BookLibrary lib) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			Toast.makeText(this.getActivity(), "sd have unmouted",
					Toast.LENGTH_LONG).show();
			return;
		}
		lib.deleteAllBook();
		SearchFileTask sft = new SearchFileTask(getActivity(), lib);
		sft.execute();
		return;
	}

	public void DisplayToast(String str) {
		Toast.makeText(this.getActivity(), str, Toast.LENGTH_SHORT).show();
	}

	private void openFile(File f) {
		Intent intent = new Intent(this.getActivity(), ReadingActivity.class);
		intent.putExtra("bookname", f.getPath());
		startActivityForResult(intent, 0);
	}

	@Override
	public void onClick(View v) {
		BookLibrary lib = new BookLibrary(this.getActivity());
		searchBook(lib);
	}

	@Override
	public void onItemClick(AdapterView<?> l, View arg1, int position, long arg3) {
		String bookname = (String) l.getAdapter().getItem(position);
		// history
		openFile(new File(bookname));
	}

	public class SearchFileTask extends AsyncTask<Void, Void, Void> {
		BookLibrary mBookLib;
		private Context mContext;
		private List<String> bookList = new ArrayList<String>();
		SearchFile.FindOneBehavior mSearchFileCallBack = new FindOneBehavior() {

			@Override
			public boolean accept(File pathname) {
				// mBookLib.addBook(pathname.getPath());
				bookList.add(pathname.getPath());
				return false;
			}
		};
		SearchFile mSearchFile;

		/**
		 * @param context
		 * @param cl
		 */
		public SearchFileTask(Context context, BookLibrary lib) {
			String exts[] = { "txt", "umd" };
			FileFilter fef = new FilenameExtFilter(exts);
			mContext = context;
			mBookLib = lib;
			mSearchFile = new SearchFileMultiThread(mSearchFileCallBack,
					Environment.getExternalStorageDirectory().getPath());
			mSearchFile.setFilter(fef);
		}

		@Override
		protected Void doInBackground(Void... params) {
			bookList.clear();
			mSearchFile.search();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(mContext, "finish", Toast.LENGTH_LONG).show();
			mFileListAdapter.setData(bookList);
			mFileListAdapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
	    super.onSaveInstanceState(outState);
	    outState.putString("DO NOT CRASH", "OK");
	}
	@Override
	public int getDrawable() {
		return R.drawable.menu_refresh;
	}

	@Override
	public void performAction(View view) {
		onClick(view);
	}
}
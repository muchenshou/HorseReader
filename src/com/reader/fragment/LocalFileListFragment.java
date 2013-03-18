package com.reader.fragment;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.reader.main.R;
import com.reader.record.BookLibrary;
import com.reader.searchfile.FileListAdapter;
import com.reader.searchfile.SearchFile;
import com.reader.searchfile.SearchFile.FindOneBehavior;
import com.reader.searchfile.SearchFileMultiThread;
import com.reader.searchfile.SearchFileSingleThread;
import com.reader.util.FilenameExtFilter;
import com.reader.main.ReadingActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mFileListAdapter.notifyDataSetChanged();
		Log.i("songlog", "mfilelistadapter:"+mFileListAdapter.getCount());
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
			Toast.makeText(this.getActivity(), "sd have unmouted",
					Toast.LENGTH_LONG).show();
			return;
		}
		lib.deleteAllBook();
		SearchFileTask sft = new SearchFileTask(getActivity(), lib);
		sft.execute(null);
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
	
	public class SearchFileTask extends AsyncTask<Void, Void, Void> {
		BookLibrary mBookLib;
		private Context mContext;
		private List<String> bookList = new ArrayList<String>();
		SearchFile.FindOneBehavior mSearchFileCallBack = new FindOneBehavior() {
			
			@Override
			public boolean accept(File pathname) {
				mBookLib.addBook(pathname.getPath());
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
			mSearchFile = new SearchFileMultiThread(mSearchFileCallBack, Environment
					.getExternalStorageDirectory().getPath());
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
}
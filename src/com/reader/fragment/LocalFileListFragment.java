package com.reader.fragment;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.reader.main.R;
import com.reader.main.ReadingActivity;
import com.reader.searchfile.FileListAdapter;
import com.reader.searchfile.SearchFile;
import com.reader.searchfile.SearchFile.FindOneBehavior;
import com.reader.searchfile.SearchFileMultiThread;
import com.reader.util.FilenameExtFilter;

public class LocalFileListFragment extends SherlockFragment implements
		View.OnClickListener, OnItemClickListener {
	ListView mListView;
	FileListAdapter mFileListAdapter;
	ProgressDialog mProgAlert;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View tv = inflater.inflate(R.layout.fileselect, container, false);
		this.mListView = (ListView) tv.findViewById(R.id.filelist);
		this.mListView.setOnItemClickListener(this);
		mListView.setScrollingCacheEnabled(false);
		if (mFileListAdapter == null)
			mFileListAdapter = new FileListAdapter(getActivity());
		mListView.setAdapter(mFileListAdapter);
		return tv;
	}

	private void searchBook() {
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			Toast.makeText(this.getActivity(), "sd have unmouted",
					Toast.LENGTH_LONG).show();
			return;
		}
		SearchFileTask sft = new SearchFileTask(getActivity());
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
		searchBook();
	}

	@Override
	public void onItemClick(AdapterView<?> l, View arg1, int position, long arg3) {
		String bookname = (String) l.getAdapter().getItem(position);
		// history
		openFile(new File(bookname));
	}

	public class SearchFileTask extends AsyncTask<Void, Void, Void> {
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
		public SearchFileTask(Context context) {
			String exts[] = { "txt", "umd" };
			FileFilter fef = new FilenameExtFilter(exts);
			mContext = context;
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
		protected void onPreExecute() {
			mProgAlert.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(mContext, "finish", Toast.LENGTH_LONG).show();
			mFileListAdapter.setData(bookList);
			mFileListAdapter.notifyDataSetChanged();
			mProgAlert.dismiss();
			super.onPostExecute(result);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("DO NOT CRASH", "OK");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mProgAlert = new ProgressDialog(getActivity());
		mProgAlert.setCanceledOnTouchOutside(false);
		mProgAlert.setCancelable(false);
		mProgAlert.setMessage("ËÑË÷ÖÐ...");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add("fresh").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		onClick(item.getActionView());
		return super.onOptionsItemSelected(item);
	}

}
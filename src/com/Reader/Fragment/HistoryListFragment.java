package com.reader.fragment;

import java.io.File;

import com.reader.record.BookHistory;
import com.reader.ui.BookAdapter;
import com.reader.main.ReadingActivity;
import com.reader.main.SearchBook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class HistoryListFragment extends Fragment implements
		OnItemClickListener {
	ListView mListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mListView = new ListView(this.getActivity());
		mListView.setOnItemClickListener(this);
		mListView.setScrollingCacheEnabled(false);
		return mListView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		BookHistory history = new BookHistory(this.getActivity());
		BookAdapter adapter = new BookAdapter(this.getActivity(),
				history.getHistory());
		mListView.setAdapter(adapter);
	}

	public void onItemClick(AdapterView<?> l, View arg1, int pos, long arg3) {
		BookAdapter adapter = (BookAdapter) l.getAdapter();
		openFile(new File((String) adapter.getItem(pos)));
	}

	private void openFile(File f) {
		if (SearchBook.isBook(f)) {
			Log.d("openFile", f.getPath());
			Intent intent = new Intent(this.getActivity(),
					ReadingActivity.class);
			intent.putExtra("bookname", f.getPath());
			startActivityForResult(intent, 0);
		}

	}

}

package com.Reader.Fragment;

import java.io.File;

import com.Reader.Main.R;
import com.Reader.Main.ReadingActivity;
import com.Reader.Main.SearchBook;
import com.Reader.Record.BookHistory;
import com.Reader.Ui.BookAdapter;

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
import android.widget.TextView;

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

	@Override
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

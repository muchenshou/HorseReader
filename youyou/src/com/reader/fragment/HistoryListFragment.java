package com.reader.fragment;

import java.io.File;

import com.reader.record.BookHistory;
import com.reader.ui.BookAdapter;
import com.reader.app.MainUi;
import com.reader.app.ReadingActivity;
import com.reader.app.MainUi.BtnStatus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class HistoryListFragment extends BaseFragment implements MainUi.OnBtnClick,
		OnItemClickListener {
	ListView mListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MainUi.BtnStatus sta = _mainui.new BtnStatus();
		sta.visible = View.INVISIBLE;
		sta.text = "·µ»Ø";
		_mainui.setLeft(sta);
		sta.visible = View.VISIBLE;
		sta.text = "Êé¿â";
		_mainui.setRight(sta);
		mListView = new ListView(this.getActivity());
		mListView.setOnItemClickListener(this);
		mListView.setScrollingCacheEnabled(false);
		BookHistory history = new BookHistory(this.getActivity());
		BookAdapter adapter = new BookAdapter(this.getActivity(),
				history.getHistory());
		mListView.setAdapter(adapter);
		return mListView;
	}

	@Override
	public void onItemClick(AdapterView<?> l, View arg1, int pos, long arg3) {
		BookAdapter adapter = (BookAdapter) l.getAdapter();
		openFile(new File((String) adapter.getItem(pos)));
	}

	@Override
	public void onStart() {
		_mainui.setOnBtnClick(this);
		super.onStart();
	}

	private void openFile(File f) {
		Intent intent = new Intent(this.getActivity(), ReadingActivity.class);
		intent.putExtra("bookname", f.getPath());
		startActivityForResult(intent, 0);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("DO NOT CRASH", "OK");
	}

	@Override
	public void onLeftBtnClick(Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRightBtnClick(Object obj) {
		_mainui.switchFragment(1);
	}
}

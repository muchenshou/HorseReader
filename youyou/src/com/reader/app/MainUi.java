package com.reader.app;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.reader.fragment.HistoryListFragment;
import com.reader.fragment.LocalFileListFragment;

public class MainUi implements View.OnClickListener {
	public interface OnBtnClick {
		void onLeftBtnClick(Object obj);

		void onRightBtnClick(Object obj);
	}

	public class BtnStatus {
		public int visible;
		public String text;
	}

	List<Fragment> mListFragment;
	protected FragmentActivity mfa;
	OnBtnClick _btnclick;
	View _mainview;
	LocalFileListFragment mLocalList;
	HistoryListFragment mHistory;

	public MainUi(FragmentActivity fa) {
		mfa = fa;
	}

	public View create(int layoutid) {
		_mainview = LayoutInflater.from(mfa).inflate(layoutid, null);
		_mainview.findViewById(R.id.left).setOnClickListener(this);
		_mainview.findViewById(R.id.right).setOnClickListener(this);
		mLocalList = new LocalFileListFragment();
		mLocalList.SetMainUi(this);
		mHistory = new HistoryListFragment();
		mHistory.SetMainUi(this);
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.fragment_container, mHistory);
		t.commit();
		return _mainview;
	}

	public View getTitle() {
		return _mainview.findViewById(R.id.title);
	}

	public void setLeft(BtnStatus str) {
		Button b = (Button) _mainview.findViewById(R.id.left);
		b.setText(str.text);
		b.setVisibility(str.visible);
	}

	public void setRight(BtnStatus str) {
		Button b = (Button) _mainview.findViewById(R.id.right);
		b.setText(str.text);
		b.setVisibility(str.visible);
	}

	public void setOnBtnClick(OnBtnClick c) {
		_btnclick = c;
	}

	private FragmentManager getSupportFragmentManager() {
		return mfa.getSupportFragmentManager();
	}

	public void switchFragment(int index) {
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
//		t.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		switch (index) {
		case 0:
			t.replace(R.id.fragment_container, mHistory);
			break;
		case 1:
			t.replace(R.id.fragment_container, mLocalList);
			t.addToBackStack(null);
			break;
		default:
			break;
		}

		t.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left:
			_btnclick.onLeftBtnClick(this);
			break;
		case R.id.right:
			_btnclick.onRightBtnClick(this);
			break;
		default:
			break;
		}
	}

}

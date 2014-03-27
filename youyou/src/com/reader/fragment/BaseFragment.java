package com.reader.fragment;

import com.reader.app.MainUi;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment{
	MainUi _mainui;
	public void SetMainUi(MainUi m) {
		_mainui = m;
	}
}

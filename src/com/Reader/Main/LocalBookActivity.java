package com.Reader.Main;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;

public class LocalBookActivity extends TabActivity {

	private TabHost mTabHost;
	private RadioGroup tabGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localbook);
		mTabHost = getTabHost();

		Intent intent1 = new Intent(this, HorseReaderActivity.class);
		createTab("阅读历史", intent1);

///		Intent intent2 = new Intent(this, FileManager.class);
	//	createTab("本地存储", intent2);

		//Intent intent3 = new Intent(this, HistoryActivity.class);
		//createTab("历史", intent3);

		mTabHost.setCurrentTab(1);
	}

	private void createTab(String text, Intent intent) {
		mTabHost.addTab(mTabHost.newTabSpec(text)
				.setIndicator(createTabView(text)).setContent(intent));
	}

	private View createTabView(String text) {
		View view = LayoutInflater.from(this).inflate(R.layout.tab_indicator,
				null);
		TextView tv = (TextView) view.findViewById(R.id.tv_tab);
		tv.setText(text);
		return view;
	}

}
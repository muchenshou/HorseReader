package com.reader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.reader.main.R;
import com.reader.ui.ActionBar;
import com.reader.ui.AdobeView;
import com.reader.ui.ActionBar.Action;

public class AdobeUiActivity extends FragmentActivity implements OnItemClickListener{
	ViewPager mPager;
	ListView listview;
	AdobeView adobe;
	ActionBar actionBar;
	/**
	 * text id displayed in list view 
	 */
	public static final int[] TITLES = { R.string.records, R.string.local,
		R.string.internet, R.string.about };
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rightview);

		// set up list view,display some choices
		listview = (ListView)findViewById(R.id.listview);
		String[] titles = new String[TITLES.length];

		// adobe view
		adobe = (AdobeView) listview.getParent();
		// retrieve the text displayed in list view
		for (int i = 0; i < titles.length; i++) {
			titles[i] = this.getString(TITLES[i]);
		}
		listview.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, titles));
		listview.setOnItemClickListener(this);
		
		// set up view pager
		mPager = (ViewPager)findViewById(R.id.content);
		mPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));
		mPager.setOnPageChangeListener(new MyPagerChangeListener());
		
		// set up action bar
		actionBar = (ActionBar)findViewById(R.id.actionbar);
		actionBar.setTitle(getResources().getString(TITLES[0]));
		actionBar.setHomeAction(new Action() {
			@Override
			public void performAction(View view) {
				adobe.switchView();
			}

			@Override
			public int getDrawable() {
				return R.drawable.cartoon_content;
			}
		});
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	public class MyFragmentAdapter extends FragmentPagerAdapter {
		final int COUNT_FRAGMENTS = 4;
		Fragment[] fragments = new Fragment[4];
		public MyFragmentAdapter(FragmentManager fm) {
			super(fm);
			fragments[0] = new HistoryListFragment();
			fragments[1] = new LocalFileListFragment();
			fragments[2] = new NetWorkListFragment();
			fragments[3] = new About();
		}
		
		@Override
		public Fragment getItem(int pos) {
			if (pos >=0 && pos<COUNT_FRAGMENTS) {
				return fragments[pos];
			}
			return null;
			
		}

		@Override
		public int getCount() {
			return COUNT_FRAGMENTS;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		mPager.setCurrentItem(arg2);
		
	}
	class MyPagerChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int pos) {
			// set title in action bar
			actionBar.setTitle(getResources().getString(TITLES[pos]));
			actionBar.removeAllActions();
			if (pos == 1){
				MyFragmentAdapter adapter = (MyFragmentAdapter) mPager.getAdapter();
				actionBar.addAction((Action)adapter.getItem(pos));
			}
		}
		
	} 
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
	    super.onSaveInstanceState(outState);
	    outState.putString("DO NOT CRASH", "OK");
	}
}
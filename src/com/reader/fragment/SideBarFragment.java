package com.reader.fragment;

import com.reader.main.R;
import com.reader.ui.ActionBar;
import com.reader.ui.ActionBar.Action;
import com.reader.ui.AdobeView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SideBarFragment extends Fragment implements OnItemClickListener {
	int mCurCheckPosition = 0;
	ListView listview;
	AdobeView adobe;
	public static final int[] TITLES = { R.string.records, R.string.local,
			R.string.internet, R.string.about };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		adobe = (AdobeView) inflater.inflate(R.layout.rightview, container,
				false);
		String[] titles = new String[TITLES.length];
		for (int i = 0; i < titles.length; i++) {
			titles[i] = this.getString(TITLES[i]);
		}
		listview = (ListView) adobe.findViewById(R.id.listview);
		listview.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_expandable_list_item_1, titles));
		listview.setOnItemClickListener(this);
		return adobe;
	}
	ActionBar actionBar;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		actionBar = (ActionBar) adobe
				.findViewById(R.id.actionbar);
		actionBar.setTitle("Home");

		actionBar.setHomeAction(new Action() {
			public void performAction(View view) {
				adobe.switchView();
			}

			public int getDrawable() {
				return R.drawable.cartoon_content;
			}
		});
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	HistoryListFragment hf = new HistoryListFragment();
	LocalFileListFragment lf = new LocalFileListFragment();
	NetWorkListFragment nf = new NetWorkListFragment();
	About about = new About();
	int cur = 0;

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.i("onItemClick", "" + arg2);
		if (arg2 == cur)
			return;
		cur = arg2;
		if (arg2 == 0) {
			FragmentManager fm = this.getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.content, hf);
			ft.commit();
			actionBar.removeAllActions();
		}
		if (arg2 == 1) {
			FragmentManager fm = this.getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.content, lf);
			ft.commit();
			actionBar.removeAllActions();
			actionBar.addAction(new Action() {
				@Override
				public void performAction(View view) {
					lf.onClick(view);
				}
				
				@Override
				public int getDrawable() {
					return R.drawable.menu_refresh;
				}
			});

		}
		if (arg2 == 2) {
			FragmentManager fm = this.getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.content, nf);
			ft.commit();
			actionBar.removeAllActions();
		}
		if (arg2 == 3) {
			FragmentManager fm = this.getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.content, about);
			ft.commit();
			actionBar.removeAllActions();
		}

	}
}

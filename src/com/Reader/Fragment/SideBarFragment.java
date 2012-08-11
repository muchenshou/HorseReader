package com.Reader.Fragment;

import com.Reader.Main.R;
import com.Reader.Main.SideBarActivity;
import com.Reader.Ui.ActionBar;
import com.Reader.Ui.ActionBar.Action;
import com.Reader.Ui.ActionBar.IntentAction;
import com.Reader.Ui.AdobeView;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

public class SideBarFragment extends Fragment implements OnItemClickListener {
	int mCurCheckPosition = 0;
	ListView listview;
	AdobeView adobe;
	// copied by hetao
	public static final String[] TITLES = { "历史记录", "本地", "网络", "关于" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		adobe = (AdobeView) inflater.inflate(R.layout.rightview, container,
				false);
		listview = (ListView) adobe.findViewById(R.id.listview);
		listview.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_expandable_list_item_1, TITLES));
		listview.setOnItemClickListener(this);
		return adobe;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final ActionBar actionBar = (ActionBar) adobe
				.findViewById(R.id.actionbar);
		actionBar.setTitle("Home");

		actionBar.setHomeAction(new Action() {
			@Override
			public void performAction(View view) {
				Toast.makeText(getActivity(), "Added action.",
						Toast.LENGTH_SHORT).show();
				adobe.switchView();
			}
			@Override
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

	@Override
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
		}
		if (arg2 == 1) {
			FragmentManager fm = this.getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.content, lf);
			ft.commit();

		}
		if (arg2 == 2) {
			FragmentManager fm = this.getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.content, nf);
			ft.commit();
		}
		if (arg2 == 3) {
			FragmentManager fm = this.getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.content, about);
			ft.commit();
		}

	}
}

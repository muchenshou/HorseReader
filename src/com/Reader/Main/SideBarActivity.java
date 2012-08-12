/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.Reader.Main;

import com.Reader.Fragment.HistoryListFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

public class SideBarActivity extends FragmentActivity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		HistoryListFragment hf = new HistoryListFragment();
		ft.add(R.id.content, hf);
		ft.commit();
	}

	/*@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (0 == requestCode) {
			Bundle bundle = null;
			if (data != null && (bundle = data.getExtras()) != null) {
				Intent intent = new Intent(SideBarActivity.this,
						ReadingActivity.class);
				intent.putExtra("bookname", bundle.getString("bookfile"));
				// startActivityForResult(intent,
				// YouYouReaderActivity.READING_RESULT_CODE);
			}
		}
		if (1 == requestCode) {
			Bundle bundle = null;
			if (data != null && (bundle = data.getExtras()) != null) {
				BookHistory history = new BookHistory(this);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}*/
}
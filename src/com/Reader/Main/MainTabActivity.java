package com.Reader.Main;

import com.Reader.Record.BookHistory;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

public class MainTabActivity extends TabActivity {
 
    private TabHost tabhost;
    private RadioGroup tabGroup;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
 
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.maintab);
        
        tabhost = getTabHost();
        tabGroup = (RadioGroup) findViewById(R.id.tab_group);
 
        // 这里新建3个的Intent用于Activity的切换
        Intent tab1 = new Intent(this, HorseReaderActivity.class);
        Intent tab2 = new Intent(this, WebActivity.class);
 
        // 向tabhost里添加tab
        tabhost.addTab(tabhost.newTabSpec("TAB1").setIndicator("本地书库")
                .setContent(tab1));
        tabhost.addTab(tabhost.newTabSpec("TAB2").setIndicator("网络书库")
                .setContent(tab2));
 
        // 给各个按钮设置监听
        tabGroup.setOnCheckedChangeListener((OnCheckedChangeListener) new OnTabChangeListener());
 
    }
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("horsereaderactivity", "here1");
		if (HorseReaderActivity.FILE_RESULT_CODE == requestCode) {
			Log.i("horsereaderactivity", "here");
			Bundle bundle = null;
			if (data != null && (bundle = data.getExtras()) != null) {
				Log.d("FILE_RESULT_CODE", "limian");

				Intent intent = new Intent(MainTabActivity.this,
						ReadingActivity.class);
				intent.putExtra("bookname", bundle.getString("bookfile"));
				startActivityForResult(intent, HorseReaderActivity.READING_RESULT_CODE);
			}
		}
		if (HorseReaderActivity.READING_RESULT_CODE == requestCode){
			Bundle bundle = null;
			if (data != null && (bundle = data.getExtras()) != null) {
				Log.d("READING_RESULT_CODE", "limian");
				BookHistory history = new BookHistory(this);
				//history.updateHistory(this.mCurrentBook,bundle.getInt("BookReading"));

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

    private class OnTabChangeListener implements OnCheckedChangeListener {
 
        public void onCheckedChanged(RadioGroup group, int id) {
            // TODO Auto-generated method stub
 
            //尤其需要注意这里，setCurrentTabByTag方法是纽带
            switch (id) {
            case R.id.tab1:
                tabhost.setCurrentTabByTag("TAB1");
                break;
            case R.id.tab2:
                tabhost.setCurrentTabByTag("TAB2");
                break;
 
            }
 
        }
    }
 
}
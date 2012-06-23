/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.Reader.Main;

import com.Reader.Record.BookHistory;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;

public class MainTabActivity extends TabActivity {
 
    private TabHost tabhost;
    private RadioGroup tabGroup;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
 
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.maintab);
        
        tabhost = getTabHost();
   //     tabGroup = (RadioGroup) findViewById(R.id.tab_group);
 
        // 这里新建3个的Intent用于Activity的切换
        Intent tab1 = new Intent(this, HorseReaderActivity.class);
        Intent tab2 = new Intent(this, WebActivity.class);
        Intent tab3 = new Intent(this, FileManager.class);
        LayoutInflater li = LayoutInflater.from(this);
        View v = li.inflate(R.layout.tabheader, null);
        ImageView iv = (ImageView)v.findViewById(R.id.tabimage);
        iv.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.cartoon_cate));
        TextView tv = (TextView)v.findViewById(R.id.tabtext);
        tv.setText("书架");
        // 向tabhost里添加tab
        tabhost.addTab(tabhost.newTabSpec("TAB1").setIndicator(v)
                .setContent(tab1));
        //tabhost.addTab(tabhost.newTabSpec("TAB2").setIndicator("网络书库")
          //      .setContent(tab2));
        v = li.inflate(R.layout.tabheader, null);
        iv = (ImageView)v.findViewById(R.id.tabimage);
        iv.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.cartoon_cate));
        tv = (TextView)v.findViewById(R.id.tabtext);
        tv.setText("本地书库");
        tabhost.addTab(tabhost.newTabSpec("TAB3").setIndicator(v)
                .setContent(tab3));
 
        // 给各个按钮设置监听
     //   tabGroup.setOnCheckedChangeListener((OnCheckedChangeListener) new OnTabChangeListener());
 
    }
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (HorseReaderActivity.FILE_RESULT_CODE == requestCode) {
			Bundle bundle = null;
			if (data != null && (bundle = data.getExtras()) != null) {
				Intent intent = new Intent(MainTabActivity.this,
						ReadingActivity.class);
				intent.putExtra("bookname", bundle.getString("bookfile"));
				startActivityForResult(intent, HorseReaderActivity.READING_RESULT_CODE);
			}
		}
		if (HorseReaderActivity.READING_RESULT_CODE == requestCode){
			Bundle bundle = null;
			if (data != null && (bundle = data.getExtras()) != null) {
				BookHistory history = new BookHistory(this);
				//history.updateHistory(this.mCurrentBook,bundle.getInt("BookReading"));

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
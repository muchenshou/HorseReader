/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.Reader.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Reader.Main.FileManager;
import com.Reader.Main.HorseReaderActivity;
import com.Reader.Main.R;
import com.Reader.Record.BookInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShelfAdapter extends BaseAdapter {
	// private Context mContext;

	public final class ViewHolder {
		public ImageView img;
		public TextView title;
	}

	public interface CallBack {
		public void CallBackOpen(String name);
	}

	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	private LayoutInflater mInflater;
	private Context ActivityContext;

	public ShelfAdapter(Context context, List<BookInfo> list) {
		ActivityContext = context;
		this.mInflater = LayoutInflater.from(context);
		getData(list);
	}

	private List<Map<String, Object>> getData(List<BookInfo> list) {
		mData.clear();
		Map<String, Object> booktitle = null;

		for (BookInfo b : list) {
			String str = b.bookName.toString()
					.substring(b.bookName.toString().lastIndexOf('.') + 1)
					.toLowerCase();
			if (str.equals("umd")) {
				booktitle = new HashMap<String, Object>();
				booktitle.put("file", b.bookName);
				booktitle.put("img",
						new Integer(com.Reader.Main.R.drawable.umd));
				booktitle.put("title", new File(b.bookName).getName());
			}
			if (str.equals("txt")) {
				booktitle = new HashMap<String, Object>();
				booktitle.put("file", b.bookName);
				booktitle.put("img",
						new Integer(com.Reader.Main.R.drawable.txt));
				booktitle.put("title", new File(b.bookName).getName());
			}
			mData.add(booktitle);
		}
		if (ActivityContext.getClass() == FileManager.class) {
			return this.mData;
		}
		return this.mData;
	}

	public int getCount() {
		if( mData.size() / 3 +1< 5){
			return 5;
		}
		return mData.size();
	}

	public Object getItem(int position) {

		return mData.get(position).get("file");
	}

	public long getItemId(int position) {
		return 0;
	}

	class BtnOnClickListener implements View.OnClickListener {
		int mIndex;

		public BtnOnClickListener(int index) {
			mIndex = index;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			((HorseReaderActivity) ActivityContext).CallBackOpen((String) mData
					.get(mIndex).get("file"));
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View layout = mInflater.inflate(R.layout.bookshelves_listitem, null);

		Button btn1 = (Button) layout.findViewById(R.id.button_1);
		if (position * 3 + 0 < mData.size()) {
			btn1.setText((String) this.mData.get(position * 3 + 0).get("title"));
			btn1.setOnClickListener(new BtnOnClickListener(position * 3 + 0));
		}

		Button btn2 = (Button) layout.findViewById(R.id.button_2);
		if (position * 3 + 1 < mData.size()) {
			btn2.setText((String) this.mData.get(position * 3 + 1).get("title"));
			btn2.setOnClickListener(new BtnOnClickListener(position * 3 + 1));
		}

		Button btn3 = (Button) layout.findViewById(R.id.button_3);
		if (position * 3 + 2 < mData.size()) {
			btn3.setText((String) this.mData.get(position * 3 + 2).get("title"));
			btn3.setOnClickListener(new BtnOnClickListener(position * 3 + 2));
		}
		return layout;
	}

}

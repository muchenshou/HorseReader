package com.Reader.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Reader.Main.FileManager;
import com.Reader.Record.BookInfo;

public class BookAdapter extends BaseAdapter {

	public final class ViewHolder {
		public ImageView img;
		public TextView title;
	}

	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	private LayoutInflater mInflater;
	private Context ActivityContext;

	public BookAdapter(Context context, List<BookInfo> list) {
		ActivityContext = context;
		this.mInflater = LayoutInflater.from(context);
		getData(list);
	}

	private List<Map<String, Object>> getData(List<BookInfo> list) {
		mData.clear();
		Map<String, Object> booktitle = null;

		for (int i = 0; i < list.size() && i < 5; i++) {
			String str = list.get(i).bookName.toString()
					.substring(list.get(i).bookName.toString().lastIndexOf('.') + 1)
					.toLowerCase();
			if (str.equals("umd")) {
				booktitle = new HashMap<String, Object>();
				booktitle.put("file", list.get(i).bookName);
				booktitle.put("img", new Integer(com.Reader.Main.R.drawable.umd));
				booktitle.put("title", new File(list.get(i).bookName).getName());
			}
			if (str.equals("txt")) {
				booktitle = new HashMap<String, Object>();
				booktitle.put("file", list.get(i).bookName);
				booktitle.put("img", new Integer(com.Reader.Main.R.drawable.txt));
				booktitle.put("title", new File(list.get(i).bookName).getName());
			}
			mData.add(booktitle);
		}
		if (ActivityContext.getClass() == FileManager.class) {
			return this.mData;
		}
		booktitle = new HashMap<String, Object>();
		booktitle.put("file", "selectfile");
		booktitle.put("img", new Integer(com.Reader.Main.R.drawable.txt));
		booktitle.put("title", "Ñ¡ÔñÎÄ¼þ");
		mData.add(booktitle);
		return this.mData;
	}

	public int getCount() {
		return mData.size();
	}

	public Object getItem(int position) {

		return mData.get(position).get("file");
	}

	public long getItemId(int position) {

		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(com.Reader.Main.R.layout.listitem, null);
			holder.img = (ImageView) convertView.findViewById(com.Reader.Main.R.id.img);
			holder.title = (TextView) convertView.findViewById(com.Reader.Main.R.id.title);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		holder.img.setBackgroundResource((Integer) mData.get(position).get(
				"img"));
		holder.title.setText((String) mData.get(position).get("title"));
		return convertView;
	}

}

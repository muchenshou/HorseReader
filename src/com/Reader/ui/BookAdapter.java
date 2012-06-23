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

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.Reader.Main.FileManager;
import com.Reader.Record.BookInfo;

public class BookAdapter extends BaseAdapter {

	public final class ViewHolder {
		public ImageView img;
		public TextView title;
		public TextView process;
	}

	private LayoutInflater mInflater;
	private Context ActivityContext;
	private List<BookInfo> mBookInfoList;

	public BookAdapter(Context context, List<BookInfo> list) {
		ActivityContext = context;
		this.mInflater = LayoutInflater.from(context);
		mBookInfoList = list;

		for (BookInfo b : mBookInfoList) {
			String str = b.bookName.toString()
					.substring(b.bookName.toString().lastIndexOf('.') + 1)
					.toLowerCase();
			if (str.equals("umd")) {
				b.mBookImage = ActivityContext.getResources().getDrawable(
						com.Reader.Main.R.drawable.umd);
			}
			if (str.equals("txt")) {
				b.mBookImage = ActivityContext.getResources().getDrawable(
						com.Reader.Main.R.drawable.txt);
			}
		}

	}

	public int getCount() {
		return this.mBookInfoList.size();
	}

	public Object getItem(int position) {
		return mBookInfoList.get(position).bookName;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(com.Reader.Main.R.layout.listitem,
					null);
			holder.img = (ImageView) convertView
					.findViewById(com.Reader.Main.R.id.img);
			holder.title = (TextView) convertView
					.findViewById(com.Reader.Main.R.id.title);
			holder.process = (TextView) convertView
					.findViewById(com.Reader.Main.R.id.process);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.img.setBackgroundDrawable(mBookInfoList.get(position)
				.getBookImage());
		String bookfulldir = mBookInfoList.get(position).bookName;
		String name = bookfulldir.substring(bookfulldir.lastIndexOf('/') + 1,
				bookfulldir.lastIndexOf('.')).toLowerCase();
		holder.title.setText(name);
		if (parent.getContext().getClass().equals(FileManager.class)) {
			holder.process.setVisibility(View.GONE);
		} else {
			holder.process.setText(mBookInfoList.get(position).mProcess);
		}
		return convertView;
	}

}

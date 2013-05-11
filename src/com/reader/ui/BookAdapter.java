/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.reader.app.R;
import com.reader.record.BookInfo;

public class BookAdapter extends BaseAdapter {

	public final class ViewHolder {
		public ImageView img;
		public TextView title;
		public TextView process;
		public TextView size;
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
						R.drawable.umd);
			}
			if (str.equals("txt")) {
				b.mBookImage = ActivityContext.getResources().getDrawable(
						R.drawable.txt);
			}
		}

	}

	@Override
	public int getCount() {
		return this.mBookInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return mBookInfoList.get(position).bookName;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem,
					null);
			holder.img = (ImageView) convertView
					.findViewById(R.id.img);
			holder.title = (TextView) convertView
					.findViewById(R.id.title);
			holder.process = (TextView) convertView
					.findViewById(R.id.process);
			holder.size = (TextView) convertView
					.findViewById(R.id.filesize);
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
		holder.size.setText(mBookInfoList.get(position).mSize / 1024 + "k");
		holder.process.setText(mBookInfoList.get(position).mProcess);
		return convertView;
	}

}

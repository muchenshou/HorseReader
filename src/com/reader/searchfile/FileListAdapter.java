package com.reader.searchfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.reader.record.BookInfo;
import com.reader.util.FileInfo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter {
	public final class ViewHolder {
		public ImageView img;
		public TextView title;
		public TextView process;
		public TextView size;
	}

	List<BookInfo> mBookInfoList = new ArrayList<BookInfo>();
	Context mContext;
	LayoutInflater mInflater;

	public FileListAdapter(Context context) {
		super();
		mContext = context;
		mBookInfoList.clear();
		mInflater = LayoutInflater.from(mContext);
	}

	public void setData(List<String> list) {
		mBookInfoList.clear();
		for (String path : list) {
			addFile(path);
		}
	}

	public synchronized void addFile(String filePath) {
		FileInfo lFileInfo = new FileInfo();
		File lFile = new File(filePath);
		lFileInfo.canRead = lFile.canRead();
		lFileInfo.canWrite = lFile.canWrite();
		lFileInfo.isHidden = lFile.isHidden();
		lFileInfo.fileName = lFile.getName();
		lFileInfo.ModifiedDate = lFile.lastModified();
		lFileInfo.IsDir = lFile.isDirectory();
		lFileInfo.filePath = filePath;
		lFileInfo.fileSize = lFile.length();
		BookInfo b = new BookInfo();
		b.bookName = lFileInfo.filePath;
		b.mSize = (int) lFileInfo.fileSize;
		String str = b.bookName.toString()
				.substring(b.bookName.toString().lastIndexOf('.') + 1)
				.toLowerCase();
		if (str.equals("umd")) {
			b.mBookImage = mContext.getResources().getDrawable(
					com.reader.app.R.drawable.umd);
		}
		if (str.equals("txt")) {
			b.mBookImage = mContext.getResources().getDrawable(
					com.reader.app.R.drawable.txt);
		}
		mBookInfoList.add(b);
	}

	@Override
	public int getCount() {
		return mBookInfoList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(com.reader.app.R.layout.listitem,
					null);
			holder.img = (ImageView) convertView
					.findViewById(com.reader.app.R.id.img);
			holder.title = (TextView) convertView
					.findViewById(com.reader.app.R.id.title);
			holder.process = (TextView) convertView
					.findViewById(com.reader.app.R.id.process);
			holder.size = (TextView) convertView
					.findViewById(com.reader.app.R.id.filesize);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.img.setBackgroundDrawable(mBookInfoList.get(position)
				.getBookImage());
		String bookfulldir = mBookInfoList.get(position).bookName;
		Log.i("songlog", bookfulldir);
		String name = bookfulldir.substring(
				bookfulldir.lastIndexOf('/') + 1,
				bookfulldir.lastIndexOf('.') == -1 ? bookfulldir.length() - 1
						: bookfulldir.lastIndexOf('.')).toLowerCase();
		holder.title.setText(name);
		holder.size.setText(mBookInfoList.get(position).mSize / 1024 + "k");
		holder.process.setText(mBookInfoList.get(position).mProcess);
		return convertView;
	}

	@Override
	public Object getItem(int position) {
		return mBookInfoList.get(position).bookName;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
}

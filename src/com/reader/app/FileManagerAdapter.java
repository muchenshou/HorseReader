/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.app;

import java.io.File;
import java.util.List;
import com.reader.app.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileManagerAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Bitmap mIcon1;
	private Bitmap mIcon2;
	private Bitmap mIcon3;
	private Bitmap mIcon4;
	private List<String> items;
	private List<String> paths;

	public FileManagerAdapter(Context context, List<String> it, List<String> pa) {
		mInflater = LayoutInflater.from(context);
		items = it;
		paths = pa;
		mIcon1 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.icon);
		mIcon2 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.icon);
		mIcon3 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.icon);
		mIcon4 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.icon);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.filerow, null);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		File f = new File(paths.get(position).toString());
		if (items.get(position).toString().equals("b1")) {
			holder.text.setText("返回根目录");
			holder.icon.setImageBitmap(mIcon1);
		} else if (items.get(position).equals("b2")) {
			holder.text.setText("返回上一层...");
			holder.icon.setImageBitmap(mIcon2);
		} else {
			holder.text.setText(f.getName());
			if (f.isDirectory()) {
				holder.icon.setImageBitmap(mIcon3);
			} else {
				holder.icon.setImageBitmap(mIcon4);
			}
		}
		return convertView;
	}

}

class ViewHolder {
	TextView text;
	ImageView icon;
}
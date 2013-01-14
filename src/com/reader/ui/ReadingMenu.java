/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.reader.command.CommandFactory;
import com.reader.main.R;
import com.reader.main.ReadingActivity;

public class ReadingMenu implements OnItemClickListener {
	PopupWindow menuDialog;// menu菜单Dialog
	GridView menuGrid;
	View menuView;
	
	List<CommandMenuItem> mMenuItemList = new ArrayList<CommandMenuItem>();

	class CommandMenuItem {
		int image;
		String text;
		int command;
	}

	void loadMenuItems() {
		this.mMenuItemList.clear();
		/** 菜单图片 **/
		int[] menu_image_array = { R.drawable.controlbar_backward_enable,
				R.drawable.controlbar_forward_enable,
				R.drawable.menu_input_pick_inputmethod,
				R.drawable.menu_syssettings };
		/** 菜单文字 **/
		String[] menu_name_array = { "上一章", "下一章", "字体大小", "设置" };
		int[] menu_command = { CommandFactory.PREPAGE, CommandFactory.NEXTPAGE,
				CommandFactory.TEXTSIZE, CommandFactory.SETTINGS };

		for (int i = 0; i < menu_image_array.length; i++) {
			CommandMenuItem menuitem = new CommandMenuItem();
			menuitem.image = menu_image_array[i];
			menuitem.text = menu_name_array[i];
			menuitem.command = menu_command[i];
			this.mMenuItemList.add(menuitem);
		}
	}

	Context mContext;

	public ReadingMenu(Context con) {
		this.mContext = con;
	}

	public void Create() {
		menuView = View.inflate(mContext, R.layout.reading_gridview_menu, null);
		// 创建AlertDialog
		this.loadMenuItems();
		menuGrid = (GridView) menuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(new MenuAdapter());
		/** 监听menu选项 **/
		menuGrid.setOnItemClickListener(this);
	}

	public void show(View pa) {
		menuDialog = new PopupWindow(mContext);// .Builder(this).create();
		menuDialog.setContentView(menuView);
		menuDialog.setFocusable(true);
		menuDialog.setWidth(LayoutParams.FILL_PARENT);
		menuDialog.setHeight(LayoutParams.WRAP_CONTENT);
		menuDialog.showAtLocation(pa, Gravity.FILL_HORIZONTAL | Gravity.BOTTOM,
				0, 0);

	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		new CommandFactory((ReadingActivity) ReadingMenu.this.mContext)
				.CreateCommand(mMenuItemList.get(pos).command).excute();

	}

	class MenuAdapter extends BaseAdapter {

		public int getCount() {
			return mMenuItemList.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.menuitem, null);
			TextView tv = (TextView) convertView.findViewById(R.id.title);
			tv.setText(mMenuItemList.get(position).text);
			ImageView image = (ImageView) convertView.findViewById(R.id.img);
			image.setBackgroundResource(mMenuItemList.get(position).image);
			return convertView;
		}

	}

}
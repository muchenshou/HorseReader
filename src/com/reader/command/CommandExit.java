/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.command;

import android.app.Activity;
import android.widget.PopupWindow;

public class CommandExit implements Command{
	private Activity activity;
	private PopupWindow popup;
	public CommandExit(Activity a,PopupWindow com){
		activity = a;
		popup = com;
	}
	@Override
	public void excute() {
		// TODO Auto-generated method stub
		popup.dismiss();
		activity.finish();
	}

}

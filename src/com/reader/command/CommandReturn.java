/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.command;

import android.widget.PopupWindow;

public class CommandReturn implements Command{
	PopupWindow popup;
	public CommandReturn(PopupWindow p){
		popup = p;
	}
	
	@Override
	public void excute() {
		// TODO Auto-generated method stub
		popup.dismiss();
	}

}

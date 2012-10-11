/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.command;

import android.view.Gravity;

import com.reader.ui.ProgressAlert;
import com.reader.main.ReadingActivity;

public class CommandTextsize implements Command{
	ReadingActivity activity;
	ProgressAlert mTextSizeProress;
	public CommandTextsize(ReadingActivity a) {
		activity = a;
	}
	public void excute() {
		// TODO Auto-generated method stub
		mTextSizeProress = new ProgressAlert(activity);
		mTextSizeProress.showAtLocation(
				((ReadingActivity) activity).bookView,
				Gravity.CENTER, 0, 0);
	}

}

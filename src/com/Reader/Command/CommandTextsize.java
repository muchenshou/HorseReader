/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.Reader.Command;

import android.view.Gravity;

import com.Reader.Main.ReadingActivity;
import com.Reader.Ui.ProgressAlert;

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

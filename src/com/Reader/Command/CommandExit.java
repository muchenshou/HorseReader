package com.Reader.Command;

import android.app.Activity;
import android.widget.PopupWindow;

public class CommandExit implements Command{
	private Activity activity;
	private PopupWindow popup;
	public CommandExit(Activity a,PopupWindow com){
		activity = a;
		popup = com;
	}
	public void excute() {
		// TODO Auto-generated method stub
		popup.dismiss();
		activity.finish();
	}

}

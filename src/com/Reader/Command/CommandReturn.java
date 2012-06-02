package com.Reader.Command;

import android.widget.PopupWindow;

public class CommandReturn implements Command{
	PopupWindow popup;
	public CommandReturn(PopupWindow p){
		popup = p;
	}
	
	public void excute() {
		// TODO Auto-generated method stub
		popup.dismiss();
	}

}

/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.command;

import com.reader.app.ReadingActivity;

public class CommandPreChapter implements Command {
	ReadingActivity activity;

	public CommandPreChapter(ReadingActivity a) {
		activity = a;
	}

	@Override
	public void excute() {

	}

}

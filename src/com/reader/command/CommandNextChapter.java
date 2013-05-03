/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.command;

import com.reader.main.ReadingActivity;

public class CommandNextChapter implements Command {
	ReadingActivity activity;

	public CommandNextChapter(ReadingActivity a) {
		activity = a;
	}

	@Override
	public void excute() {
	}

}

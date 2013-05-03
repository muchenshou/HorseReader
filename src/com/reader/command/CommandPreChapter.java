/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.command;

import com.reader.book.bookview.BookView;
import com.reader.book.manager.BookManager;
import com.reader.book.umd.UmdBook;
import com.reader.main.ReadingActivity;

public class CommandPreChapter implements Command {
	ReadingActivity activity;

	public CommandPreChapter(ReadingActivity a) {
		activity = a;
	}

	@Override
	public void excute() {

	}

}

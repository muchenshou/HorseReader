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

public class CommandNextChapter implements Command {
	ReadingActivity activity;

	public CommandNextChapter(ReadingActivity a) {
		activity = a;
	}

	public void excute() {
		BookManager _bookmanager = activity.bookmanager;
		UmdBook umd = (UmdBook) _bookmanager.getBook();
		BookView bookView = activity.bookView;
		int value = umd.getChapterLocal(umd.localIsInWhichChapter(_bookmanager.getReadingPosition()));
		bookView.setLocal(value);
		bookView.postInvalidate();
		bookView.refreshDrawableState();
	}

}

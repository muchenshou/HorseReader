/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.Reader.Command;

import com.Reader.Book.BookView.BookView;
import com.Reader.Book.Manager.BookManager;
import com.Reader.Book.Umd.UmdBook;
import com.Reader.Main.ReadingActivity;

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

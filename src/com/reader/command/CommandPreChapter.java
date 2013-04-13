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
		// TODO Auto-generated method stub
		BookManager _bookmanager = activity.bookmanager;
		UmdBook umd = (UmdBook) _bookmanager.getBook();
		BookView bookView = _bookmanager.getBookView();
		int chapter = umd.localIsInWhichChapter(_bookmanager
				.getReadingPosition());
		if (chapter == 0) {
			bookView.setLocal(0);
			bookView.postInvalidate();
			bookView.refreshDrawableState();
			return;
		}
		int value = umd.getChapterLocal(chapter - 3);
		bookView.setLocal(value);
		bookView.postInvalidate();
		bookView.refreshDrawableState();
	}

}

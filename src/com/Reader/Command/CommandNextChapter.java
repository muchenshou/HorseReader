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
		BookView bookView = _bookmanager.getBookView();
		//int value = umd.getChapterLocal(umd.localIsInWhichChapter(bookView
		//		.getTextUtil().getCurLocal()));
		//bookView.getTextUtil().setLocal(value);
		activity.setLookingBookView();
	}

}

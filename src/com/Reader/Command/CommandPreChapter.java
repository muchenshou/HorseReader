package com.Reader.Command;

import com.Reader.Book.BookView.BookView;
import com.Reader.Book.Manager.BookManager;
import com.Reader.Book.Umd.UmdBook;
import com.Reader.Main.ReadingActivity;

public class CommandPreChapter implements Command {
	ReadingActivity activity;

	public CommandPreChapter(ReadingActivity a) {
		activity = a;
	}

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

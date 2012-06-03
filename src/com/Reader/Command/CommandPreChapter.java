package com.Reader.Command;

import com.Reader.Book.BookView.BookView;
import com.Reader.Book.Manager.BookManager;
import com.Reader.Book.Umd.UmdBook;
import com.Reader.Main.ReadingActivity;

public class CommandPreChapter implements Command{
	ReadingActivity activity;
	public CommandPreChapter(ReadingActivity a){
		activity = a;
	}

	public void excute() {
		// TODO Auto-generated method stub
		BookManager _bookmanager = activity.bookmanager;
		UmdBook umd = (UmdBook) _bookmanager.getBook();
		BookView bookView = _bookmanager.getBookView();
		int value = umd.getChapterLocal(umd.localIsInWhichChapter(_bookmanager.getReadingPosition()));
		value = umd.getChapterLocal(umd.localIsInWhichChapter(value - 1));
		bookView.setLocal(value);
		bookView.postInvalidate();
	}

}

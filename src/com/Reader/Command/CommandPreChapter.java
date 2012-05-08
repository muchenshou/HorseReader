package com.Reader.Command;

import com.Reader.Main.ReadingActivity;

public class CommandPreChapter implements Command{
	ReadingActivity activity;
	public CommandPreChapter(ReadingActivity a){
		activity = a;
	}

	public void excute() {
		// TODO Auto-generated method stub
		//BookManager _bookmanager = activity.bookmanager;
		//UmdBook umd = (UmdBook) _bookmanager.getBook();
		//BookView bookView = _bookmanager.getBookView();
		//bookView.getTextUtil().setLocal(
		//		umd.getChapterLocal(umd.localIsInWhichChapter(bookView
		//				.getTextUtil().getCurLocal()) - 2));
		activity.setLookingBookView();
	}

}

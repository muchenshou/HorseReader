package com.reader.command;

import com.reader.app.ReadingActivity;

public class CommandFactory {
	public static final int NEXTLINE = 0;
	public static final int NEXTPAGE = 1;
	public static final int JUMP = 2;
	public static final int TEXTSIZE = 3;
	public static final int PREPAGE = 4;
	public static final int PRECHAPTER = 5;
	public static final int NEXTCHAPTER = 6;
	public static final int EXIT = 7;
	public static final int RETURN = 8;
	public static final int BAIFENBI = 9;
	public static final int NIGHTMODE = 10;
	public static final int MORE = 11;
	public static final int SETTINGS = 12;
	ReadingActivity mReadActivity;

	public CommandFactory(ReadingActivity read) {
		mReadActivity = read;
	}

	public Command CreateCommand(int com) {

		if (com == PREPAGE)
			return new CommandPreChapter(this.mReadActivity);
		if (com == NEXTPAGE)//
			return new CommandNextChapter(this.mReadActivity);
		if (com == TEXTSIZE)
			return new CommandTextsize(this.mReadActivity);
		/*
		 * if(com== TEXTSIZE)// mTextSizeProress = new ProgressAlert(mContext );
		 * mTextSizeProress. showAtLocation( ((ReadingActivity)
		 * mContext).bookView, Gravity.CENTER, 0, 0);
		 */
		return null;
	}
}

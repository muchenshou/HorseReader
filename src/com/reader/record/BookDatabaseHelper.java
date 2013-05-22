/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.record;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDatabaseHelper extends SQLiteOpenHelper {

	public static final String ID = "_id";
	public static final String FILEFULLNAME = "fulldirname";
	public static final String FILESIZE = "filesize";
	public static final String DATABASE = "booklibrary";
	public static final String TB_HISTORY = "bookhistory";
	public static final String TB_HISTORY_PARAGRAPH = "paragraph";
	public static final String TB_HISTORY_CHARINDEX = "char";
	public static final String TB_HISTORY_REALPOS = "realpos";
	public static final String TB_HISTORY_TIME = "time";
	public static final String TB_HISTORY_PROCESS = "process";

	public static final String[] HISTORY_POSITION = { TB_HISTORY_PARAGRAPH,
			TB_HISTORY_CHARINDEX, TB_HISTORY_REALPOS };

	public BookDatabaseHelper(Context context, CursorFactory factory,
			int version) {
		super(context, DATABASE, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_HISTORY + "(" + ID
				+ " INTEGER PRIMARY KEY," + FILEFULLNAME + " VARCHAR,"
				+ TB_HISTORY_PROCESS + " VARCHAR," + TB_HISTORY_PARAGRAPH
				+ " INTEGER DEFAULT 0," + TB_HISTORY_CHARINDEX
				+ " INTEGER DEFAULT 0," + TB_HISTORY_REALPOS
				+ " INTEGER DEFAULT 0," + FILESIZE + " INTEGER DEFAULT 0,"
				+ TB_HISTORY_TIME
				+ " TimeStamp NOT NULL DEFAULT (datetime('now','localtime'))"
				+ ");");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}

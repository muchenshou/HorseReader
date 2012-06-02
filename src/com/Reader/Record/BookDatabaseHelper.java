package com.Reader.Record;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDatabaseHelper extends SQLiteOpenHelper{
	
	public static final String TB_BOOKS = "bookfiles";
	public static final String ID = "_id";
	private static final String FILENAME = "filename";
	private static final String FILEFULLNAME = "fulldirname";
	private static final String DATABASE = "booklibrary";
	private static final String TB_HISTORY = "bookhistory";
	private static final String TB_HISTORY_BOOKID = "bookid";
	private static final String TB_HISTORY_POSITION = "position";
	private static final String TB_HISTORY_TIME ="time";
	public BookDatabaseHelper(Context context,
			CursorFactory factory, int version) {
		super(context, DATABASE, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TB_BOOKS + "("
				+ ID + " INTEGER PRIMARY KEY,"
				+ FILENAME + " VARCHAR,"
				+ FILEFULLNAME + " VARCHAR"
				+ ");");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TB_HISTORY + "("
				+ ID + " INTEGER PRIMARY KEY,"
				+ TB_HISTORY_BOOKID +" INTEGER,"
				+ TB_HISTORY_POSITION + " INTEGER DEFAULT 0,"
				+ TB_HISTORY_TIME + " TimeStamp NOT NULL DEFAULT (datetime('now','localtime'))"
				+ ");");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	
}
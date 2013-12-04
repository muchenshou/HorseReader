/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.record;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class BookHistory {
	BookDatabaseHelper bookHelper;

	public BookHistory(Context context) {
		this.bookHelper = new BookDatabaseHelper(context, null, 1);
	}

	public List<BookInfo> getHistory() {
		SQLiteDatabase db = this.bookHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select _id,fulldirname,process,filesize from bookhistory",
				null);
		List<BookInfo> list = new ArrayList<BookInfo>();
		while (cursor.moveToNext()) {
			BookInfo info = new BookInfo();
			info.book_id = cursor.getInt(0);
			info.bookName = cursor.getString(1);
			info.mProcess = cursor.getString(2);
			info.mSize = cursor.getInt(3);
			list.add(info);
		}
		cursor.close();
		db.close();
		return list;
	}

	private SQLiteStatement myStorePositionStatement;


	public boolean exist(String book) {
		SQLiteDatabase db = this.bookHelper.getReadableDatabase();
		Cursor cur = db.rawQuery(String.format(
				"select * from bookhistory where fulldirname=\"%s\"", book),
				null);
		boolean exist = cur.moveToNext();
		cur.close();
		db.close();
		return exist;
	}


	// private void updatePos(String bookname, BookPosition pos) {
	// SQLiteDatabase db = this.bookHelper.getWritableDatabase();
	// if (pos != null)
	// db.execSQL(String
	// .format("update bookhistory set time=datetime('now','localtime'),position=%d where fulldirname=\"%s\";",
	// pos, bookname));
	// else
	// db.execSQL(String
	// .format("update bookhistory set time=datetime('now','localtime') where fulldirname=\"%s\";",
	// bookname));
	// db.close();
	// }

	// private void updateProcess(String bookname, String pos) {
	// SQLiteDatabase db = this.bookHelper.getWritableDatabase();
	// if (pos != null) {
	// db.execSQL(String
	// .format("update bookhistory set time=datetime('now','localtime'),process=\"%s\" where fulldirname=\"%s\";",
	// pos, bookname));
	// } else
	// db.execSQL(String
	// .format("update bookhistory set time=datetime('now','localtime') where fulldirname=\"%s\";",
	// bookname));
	// db.close();
	// }

}

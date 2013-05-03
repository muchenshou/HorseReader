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

	private void addHistory(String book, int pos) {
		SQLiteDatabase db = this.bookHelper.getReadableDatabase();
		db.execSQL(String.format(
				"insert into bookhistory(fulldirname,position) values(\"%s\",%d);",
				book, pos));

		db.close();

	}

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

	public void updateHistory(String bookname, int pos) {
		// TODO Auto-generated method stub
		if (this.exist(bookname)) {
			updatePos(bookname, pos);
		} else {
			this.addHistory(bookname, 0);
		}
	}

	public void updateHistoryPro(String bookname, String pos) {
		// TODO Auto-generated method stub
		if (this.exist(bookname)) {
			updateProcess(bookname, pos);
		} else {
			this.addHistory(bookname, 0);
		}
	}

	public int getPosition(String book) {
		SQLiteDatabase db = this.bookHelper.getReadableDatabase();
		Cursor cur = db
				.rawQuery(
						String.format(
								"select position from bookhistory where fulldirname=\"%s\"",
								book), null);
		int rtn = 0;
		if (cur.moveToNext()) {
			rtn = cur.getInt(0);
		}
		cur.close();
		db.close();
		return rtn;
	}

	private void updatePos(String bookname, int pos) {
		SQLiteDatabase db = this.bookHelper.getWritableDatabase();
		if (pos != -1)
			db.execSQL(String
					.format("update bookhistory set time=datetime('now','localtime'),position=%d where fulldirname=\"%s\";",
							pos, bookname));
		else
			db.execSQL(String
					.format("update bookhistory set time=datetime('now','localtime') where fulldirname=\"%s\";",
							bookname));
		db.close();
	}

	private void updateProcess(String bookname, String pos) {
		SQLiteDatabase db = this.bookHelper.getWritableDatabase();
		if (pos != null) {
			db.execSQL(String
					.format("update bookhistory set time=datetime('now','localtime'),process=\"%s\" where fulldirname=\"%s\";",
							pos, bookname));
		} else
			db.execSQL(String
					.format("update bookhistory set time=datetime('now','localtime') where fulldirname=\"%s\";",
							bookname));
		db.close();
	}

}

/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.Reader.Record;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BookLibrary {

	BookDatabaseHelper bookHelper;

	public BookLibrary(Context context) {
		this.bookHelper = new BookDatabaseHelper(context, null, 1);
	}

	public List<BookInfo> readLibrary(){
		SQLiteDatabase db = this.bookHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select _id,fulldirname,filesize from bookfiles", null);
		List<BookInfo> list = new ArrayList<BookInfo>();
		while (cursor.moveToNext()) {
			BookInfo info = new BookInfo();
			info.book_id = cursor.getInt(0);
			info.bookName = cursor.getString(1);
			info.mSize =  cursor.getInt(2);
			list.add(info);
		}
		cursor.close();
		db.close();
		return list;
	}

	public boolean existBook(String bookname) {
		SQLiteDatabase db = this.bookHelper.getReadableDatabase();
		Cursor cursor = db
				.rawQuery("select _id,filename from bookfiles where fillname="
						+ bookname, null);
		boolean have = cursor.moveToNext();
		cursor.close();
		db.close();
		return have;
	}

	public void deleteAllBook() {
		SQLiteDatabase db = bookHelper.getWritableDatabase();
		db.execSQL("delete from bookfiles;");
		db.close();

	}

	public void addBook(String record) {
		SQLiteDatabase db = bookHelper.getWritableDatabase();
		File file = new File(record);
		Log.i("booklib", record);
		Log.i("booklib", record.substring(record.lastIndexOf('/') + 1));
		db.execSQL("insert into bookfiles(filename,fulldirname,filesize) values ("
				+ "\""
				+ record.substring(record.lastIndexOf('/') + 1)
				+ "\""
				+ "," + "\"" + record + "\"" + "," + file.length() + ")");
		db.close();
	}
}

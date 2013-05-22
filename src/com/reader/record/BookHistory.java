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

import com.reader.book.manager.BookPosition;

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

	public void storePosition(String book, BookPosition position) {
		if (position == null)
			position = new BookPosition(0, 0, 0);
		
		if (exist(book)) {
			SQLiteDatabase db = this.bookHelper.getReadableDatabase();
			db.execSQL(String
					.format("update bookhistory set time=datetime('now','localtime'),paragraph=%d,char=%d,realpos=%d where fulldirname=\"%s\";",
							position.mElementIndex, position.mOffset,
							position.mRealBookPos, book));
			db.close();
			return;
		}
		SQLiteDatabase db = this.bookHelper.getReadableDatabase();
		if (myStorePositionStatement == null) {
			myStorePositionStatement = db
					.compileStatement("INSERT OR REPLACE INTO bookhistory (fulldirname,paragraph,char,realpos) VALUES (?,?,?,?)");
		}
		myStorePositionStatement.bindString(1, book);
		myStorePositionStatement.bindLong(2, position.mElementIndex);
		myStorePositionStatement.bindLong(3, position.mOffset);
		myStorePositionStatement.bindLong(4, position.mRealBookPos);
		myStorePositionStatement.execute();
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

	public BookPosition getPosition(String book) {
		SQLiteDatabase db = this.bookHelper.getReadableDatabase();
		BookPosition position = new BookPosition(0, 0, 0);
		Cursor cur = db.query(BookDatabaseHelper.TB_HISTORY,
				BookDatabaseHelper.HISTORY_POSITION, "fulldirname=?",
				new String[] { book }, null, null, null);
		if (cur.moveToNext()) {
			position.mElementIndex = cur.getInt(0);
			position.mOffset = cur.getInt(1);
			position.mRealBookPos = cur.getInt(2);
		}
		cur.close();
		db.close();
		return position;
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

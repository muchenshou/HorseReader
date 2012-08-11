/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.Reader.Book.Manager;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.Reader.Main.R;
import com.Reader.Main.ReadingActivity;
import com.Reader.Book.BookView.BookView;
import com.Reader.Book.BookView.PageObj;
import com.Reader.Config.PageConfig;
import com.Reader.Book.Book;
import com.Reader.Book.BookFactory;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class BookManager{

	public static final int OPENBOOK = 7;
	public static final int BUFSIZE = 512;
	private Book book;
	private BookView bookView;
	public ReadingActivity bookActivity;

	int bufferlocal = -1;// 文本块的位置
	int BUFLEN = 4 * 1024;

	int position = 0;

	public BookManager(Context con, File file) throws IOException {
		bookActivity = (ReadingActivity) con;
		book = BookFactory.createBook(file);
		bookView = new BookView(con,book);
		bookView.setFocusable(true);

	}

	public BookView getBookView() {
		return bookView;
	}

	
	public void openBook(int position) throws IOException {
		book.openBook();
		book.openOffset = position;
		this.bookView.setBgBitmap(BitmapFactory.decodeResource(
				bookActivity.getResources(), R.drawable.bg));

	}

	public void closeBook() {
		book.closeBook();
	}

	public int getBookSize() {
		return book.size();
	}

	

	public Book getBook() {
		return book;
	}

	public String getReadingContent(){
		return this.bookView.bookreading.getCurContent();
	}
	public int getReadingPosition() {
		return this.bookView.bookreading.getCurPosition();
	}

}

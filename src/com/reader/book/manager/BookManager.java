/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.manager;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.reader.main.R;
import com.reader.book.bookview.BookView;
import com.reader.book.Book;
import com.reader.book.BookFactory;
import com.reader.main.ReadingActivity;

public class BookManager{

	public static final int OPENBOOK = 7;
	public static final int BUFSIZE = 512;
	private Book book;
	private BookView bookView;

	int bufferlocal = -1;
	int BUFLEN = 4 * 1024;

	int position = 0;

	public BookManager(Context con, File file) throws IOException {
		book = BookFactory.createBook(file);
	}
	
	public Book openBook(int position) throws IOException {
		book.openBook();
		book.openOffset = position;
		return book;
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
}

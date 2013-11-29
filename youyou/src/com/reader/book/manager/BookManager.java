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

import com.reader.book.Book;
import com.reader.book.model.BookModel;
import com.reader.code.umd.UmdBook;
import com.reader.view.GLView;

public class BookManager {

	public static final int OPENBOOK = 7;
	public static final int BUFSIZE = 512;
	private Book book;

	int bufferlocal = -1;
	int BUFLEN = 4 * 1024;
	int position = 0;
	
	public static GLView View;
	public static BookModel Model;
	public BookManager(Context con, File file) throws IOException {
		book = BookManager.createBook(file);
	}

	public BookModel openBook(BookPosition position) throws IOException {
		book.openBook();
		book.openOffset = position;
		Model = new BookModel(book);
		return Model;
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

	public static Book createBook(File file) throws IOException {
		String str = file.toString()
				.substring(file.toString().lastIndexOf('.') + 1).toLowerCase();
		if (str.equals("umd")) {
			return new UmdBook(file);
		}
		return null;
	
	}
}

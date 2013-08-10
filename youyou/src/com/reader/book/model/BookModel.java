package com.reader.book.model;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import com.reader.book.AreaDraw;
import com.reader.book.Book;
import com.reader.book.Line;
import com.reader.book.Page;
import com.reader.book.manager.BookManager;
import com.reader.code.text.GBKTextReader;
import com.reader.config.PageConfig;
import com.reader.util.InputStreamWithCount;

public class BookModel {
	public Book mBook;

	public BookModel(Book book) {
		mBook = book;
		reader = new GBKTextReader(book.inputStream());
		// -----------------------
		// 需要解析，
		// reader.close()
		// -----------------------
	}

	GBKTextReader reader;
	// 每一页的位置存储在这里 ///
	int pagePos[];

	public Page getPage(int index) {
		reader = new GBKTextReader(mBook.inputStream());
		Page page = new Page();
		while (reader.hasNext()) {
			AreaDraw area = reader.next().toDrawArea();
			Line l = new Line();
		}
		return page;
	}
}

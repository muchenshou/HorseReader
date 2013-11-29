package com.reader.book.model;

import com.reader.book.Book;

public class BookModel {
	public Book mBook;

	public BookModel(Book book) {
		mBook = book;
		// -----------------------
		// 需要解析，
		// reader.close()
		// -----------------------
	}

	// 每一页的位置存储在这里 ///
	/*
	 * 现在表示书籍位置的类型是int型，
	 * 将来肯定会根据不同格式书有不同的表示类型
	 * */
	int pagePos[];

	
}

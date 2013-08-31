package com.reader.book.model;

import com.reader.book.AreaDraw;
import com.reader.book.Book;
import com.reader.book.Page;
import com.reader.code.text.GBKTextReader;

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
	/*
	 * 现在表示书籍位置的类型是int型，
	 * 将来肯定会根据不同格式书有不同的表示类型
	 * */
	int pagePos[];

	public Page getPage(int index) {
		/*
		 * 这一段其实主要是根据reader读取每一个MarkupElement，
		 * MarkupElement 生成DrawArea，并根据DrawArea对象的大小
		 * 放置相应的DrawArea位置，并把每一页的起始位置放在pagePos中
		 * */
		reader = new GBKTextReader(mBook.inputStream());
		Page page = new Page();
		while (reader.hasNext()) {
			AreaDraw area = reader.next().toDrawArea();
		}
		return page;
	}
}

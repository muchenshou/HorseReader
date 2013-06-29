package com.reader.book.model;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import com.reader.book.AreaDraw;
import com.reader.book.Book;
import com.reader.book.Page;

public class BookModel {
	public Book mBook;
	BlockingQueue<MarkupElement> mElements = new LinkedBlockingQueue<MarkupElement>();
	LinkedList<AreaDraw> mLines = new LinkedList<AreaDraw>();

	public BookModel(Book book) {
		mBook = book;
	}

	private void pushIntoElementsList() {
		mElements.clear();
	}

	public void pushIntoPagesList(CopyOnWriteArrayList<Page> pages) {
		pushIntoElementsList();
		mBook.pushIntoList(mElements, pages, mLines);
	}
}

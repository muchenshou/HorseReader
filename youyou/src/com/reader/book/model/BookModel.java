package com.reader.book.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.util.Log;

import com.reader.book.AreaDraw;
import com.reader.book.Book;
import com.reader.book.Line;
import com.reader.book.Page;
import com.reader.book.manager.BookManager;
import com.reader.config.PageConfig;

public class BookModel {
	public Book mBook;
	BlockingQueue<MarkupElement> mElements = new LinkedBlockingQueue<MarkupElement>();
	LinkedList<AreaDraw> mLines = new LinkedList<AreaDraw>();

	public BookModel(Book book) {
		mBook = book;
	}

	private void pushIntoElementsList() {
		mElements.clear();
		mBook.pushIntoList(mElements);
	}

	public void pushIntoPagesList(List<Page> pages) {
		pushIntoElementsList();
		Iterator<MarkupElement> iter = mElements.iterator();
		while (iter.hasNext()) {
			MarkupElement element = iter.next();
			element.pushIntoLines(mLines, pages);
		}
		
	}
}

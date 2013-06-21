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
		int i = 0;
		while (iter.hasNext()&&i++<100 ) {
			MarkupElement element = iter.next();
			element.pushIntoLines(mLines);
		}
		float flag = 0.0f;
		final float screenHeight = BookManager.View.getHeight() - 20;
		Page page = new Page(this);
		for (AreaDraw a : mLines) {
			flag += a.getHeight();
			if (flag<screenHeight) {
				page.addLine(a);
			}else{
				pages.add(page);
				page = new Page(this);
				page.addLine(a);
				flag = a.getHeight();
			}
		}
	}
}

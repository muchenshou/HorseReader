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

public class BookModel {
	Book mBook;
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
		int flag = 0;

		Page page = new Page(this);
		for (AreaDraw a : mLines) {
			flag++;
			page.addLine(a);
			if (flag == 17) {
				pages.add(page);
				page = new Page(this);
				flag = 0;
			}
		}
	}
}

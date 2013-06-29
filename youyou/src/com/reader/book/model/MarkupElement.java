package com.reader.book.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.reader.book.AreaDraw;
import com.reader.book.Book;
import com.reader.book.Line;
import com.reader.book.Page;
import com.reader.book.manager.BookManager;

public abstract class MarkupElement {
	enum TYPE {
		TEXT, IMAGE, NEWLINE
	}

	enum STATUS {

	}

	Cursor mElementCursor = new Cursor();
	public int index;
	TYPE type = TYPE.TEXT;
	Book mBook;

	public Cursor getElementCursor() {
		return mElementCursor;
	}

	public abstract int getLength();

	public int size() {
		return mElementCursor.mRealFileLast - mElementCursor.mRealFileStart + 1;
	}

	public abstract void fill();

	private static float flag = 0.0f;
	final float screenHeight = BookManager.View.getHeight() - 20;

	private void addLineIntoPage(AreaDraw line, CopyOnWriteArrayList <Page> pages) {
		Page page;
		if (flag == 0.0f) {
			page = new Page();
			pages.add(page);
		}else {
			page = pages.get(pages.size()-1);
		}
		flag += line.getHeight();
		if (flag < screenHeight) {
			line.setPos(0.0f, flag - line.getHeight());
			page.add(line);
		} else {
			page = new Page();
			line.setPos(.0f, .0f);
			page.add(line);
			pages.add(page);
			flag = line.getHeight();
		}
	}

	public void pushIntoLines(List<AreaDraw> lines, CopyOnWriteArrayList<Page> pages) {
		AreaDraw next;
		int offset = 0;
		fill();
		do {
			next = new Line(offset, this);
			next.fill();
			if ((next.offset + next.length) >= getLength()) {
				lines.add(next);
				addLineIntoPage(next, pages);
				return;
			} else {
				offset = next.offset + next.length;
			}
			lines.add(next);
			addLineIntoPage(next, pages);
		} while (true);

	}

}

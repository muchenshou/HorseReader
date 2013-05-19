/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Paint;

import com.reader.book.Book;
import com.reader.book.Line;
import com.reader.book.Page;
import com.reader.book.PageBuffer;
import com.reader.book.bookview.BookView;
import com.reader.book.model.BookModel;
import com.reader.book.model.Element;
import com.reader.book.model.ParagraphElement;
import com.reader.config.PageConfig;

public class BookContent {
	int mStart = 0;
	int mEnd = 0;
	public int pageline = 5;
	private float pageWidth = (float) 0.0;
	private float pageHeight = (float) 0.0;
	private Paint mPaint = null;
	PageBuffer mPageBuffer = new PageBuffer();
	BookModel mBookModel;
	public Book mBook = null;

	public BookContent(Book book) {
		mBook = book;
		mBookModel = new BookModel(mBook);
		this.mPaint = PageConfig.pagePaintFromConfig(false);
	}

	public String getCurContent() {
		return "nothing";
	}


	public int getLineHeight() {
		return BookView.getTextHeight(mPaint) + PageConfig.getPadding();
	}

	public void update(int w, int h) {
		pageHeight = h;
		pageWidth = w;
	}

	public void update() {
		pageline = (int) (pageHeight / getLineHeight());
	}

	public Page mCurPage;
	public Element mCurElement;
	public void setCurPosition(BookPosition cur) {
		mCurPage = getPageStr(cur);
	}

	public List<String> getNextPage() {

	}

	public List<String> getPrePage() {
		int pos = getprePagePosition();
		return pos != -1 ? getPageStr(new BookPosition(pos)) : null;
	}

	public void turnToPre() {
		setCurPosition(new BookPosition(getprePagePosition()));
	}

	public void turnToNext() {
		setCurPosition(new BookPosition(getNextPagePosition()));
	}

	private synchronized Page getPageStr(BookPosition position) {
		final Page page = new Page();
		page.clear();
		Element.Iterator iter = mBookModel.iterator(position.mElementIndex, position.mRealBookPos);
		Element element = iter.next();
		if (element instanceof ParagraphElement) {
			List<Line> lines = ((ParagraphElement)element).toLines();
			java.util.Iterator<Line> lineIter = lines.iterator();
			for (; page.getLinesSize() < pageline ;) {
				if (lineIter.hasNext()) {
					page.addLine(lineIter.next());
				} else {
					if (iter.hasNext()) {
						element = iter.next();
						lines = ((ParagraphElement) element).toLines();
						lineIter = lines.iterator();
					}
					else
						break;
				}
			}
		}
		return page;
	}

	public BookPosition getNextPagePosition() {
		return null;
	}

	public boolean isBookEnd() {
		return false;
	}

	public boolean isBookStart() {
		return false;
	}

	private int getprePagePosition() {
		int curPosition = mCurPosition.mPostion;
		Page page;
		if ((page = mPageBuffer.existPrePage(curPosition)) != null) {
			return page.getPageStartPosition();
		}
		LinkedList<Line> prepage = new LinkedList<Line>();
		if (this.isBookStart()) {
			return 0;
		}
		this.preLinePosition(curPosition);
		prepage.addAll(0, this.mBufLine);
		for (; prepage.size() < this.pageline && !isBookEnd();) {
			preLinePosition(prepage.get(0).mStart);
			prepage.addAll(0, this.mBufLine);
		}
		page = new Page();
		page.clear();
		for (Line l : prepage) {
			page.addLine(l);
		}

		mPageBuffer.addPage(page);
		return page.getPageStartPosition();
	}

}
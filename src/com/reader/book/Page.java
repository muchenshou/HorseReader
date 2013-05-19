package com.reader.book;

import java.util.LinkedList;
import java.util.List;

import com.reader.book.manager.BookPosition;

public class Page implements Comparable<Page> {
	LinkedList<Line> mLines = new LinkedList<Line>();

	public List<String> getStrings() {
		List<String> strs = new LinkedList<String>();
		for (int i = 0; i < mLines.size(); i++) {
			strs.add(mLines.get(i).strLine.toString());
		}
		return strs;
	}
	public boolean isNull () {
		return mLines.size() == 0;
	}

	public int getLinesSize() {
		return mLines.size();
	}

	public void clear() {
		mLines.clear();
	}

	public void addLine(Line line) {
		mLines.add(line);
	}

	@Override
	public int compareTo(Page another) {

		return 0;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
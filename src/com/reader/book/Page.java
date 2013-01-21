package com.reader.book;

import java.util.LinkedList;
import java.util.List;

public class Page implements Comparable<Page>{
	LinkedList<Line> mLines = new LinkedList<Line>();

	public List<String> getStrings() {
		List<String> strs = new LinkedList<String>();
		for (int i = 0; i < mLines.size(); i++) {
			strs.add(mLines.get(i).strLine.toString());
		}
		return strs;
	}

	public int getPageStartPosition() {
		if (mLines.size() == 0) {
			return -1;
		}
		return mLines.get(0).mStart;
	}

	public int getPageEndPosition() {
		if (mLines.size() == 0) {
			return -1;
		}
		return mLines.get(mLines.size() - 1).getEnd();
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
		if (this.getLinesSize() == 0 || another.getLinesSize() == 0) {
			return -1;
		}
		if (this.getPageStartPosition() != this.getPageStartPosition()) {
			return -1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		for (Line l:mLines) {
			str.append(l.strLine);
		}
		return str.toString();
	}
}
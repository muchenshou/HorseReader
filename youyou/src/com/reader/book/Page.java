package com.reader.book;

import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Page extends AreaDraw {

	public Page() {
	}

	public List<AreaDraw> getAreasDraw() {
		return mLines;
	}

	@Override
	public void add(AreaDraw line) {
		mLines.add(line);
	}

	@Override
	public float getWidth() {
		return 0;
	}

	@Override
	public float getHeight() {
		return 0;
	}

	@Override
	public void draw(Canvas canvas, float left, float top, Paint paint) {
		Iterator<AreaDraw> iter = createIterator();
		while (iter.hasNext()) {
			AreaDraw area = iter.next();
			area.draw(canvas, area.getX(), area.getY(), paint);
		}
	}

	@Override
	public void fill() {
		Iterator<AreaDraw> iter = createIterator();
		while (iter.hasNext()) {
			AreaDraw area = iter.next();
			area.fill();
		}
	}

	@Override
	public Iterator<AreaDraw> createIterator() {
		return mLines.iterator();
	}

	@Override
	public boolean hasChild() {
		return true;
	}
}
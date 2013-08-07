package com.reader.book;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.reader.book.manager.BookManager;
import com.reader.config.PageConfig;

public class Line extends AreaDraw {

	public Line() {
	}

	@Override
	public float getWidth() {
		return BookManager.View.getWidth();
	}

	@Override
	public float getHeight() {
		return PageConfig.getTextHeight(PageConfig.pagePaintFromConfig(false))
				+ PageConfig.getPadding();
	}

	@Override
	public void draw(Canvas canvas, float left, float top, Paint paint) {
		float X = left, Y = top;
		for (AreaDraw a : _list) {
			a.draw(canvas, X, Y, paint);
			X += (float) a.getWidth();
		}
	}

	List<AreaDraw> _list = new ArrayList<AreaDraw>();

	@Override
	public void add(AreaDraw area) {
		_list.add(area);
	}

	@Override
	public void fill() {
		throw new UnsupportedOperationException();
	}
}
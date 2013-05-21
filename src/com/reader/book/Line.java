package com.reader.book;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.reader.book.bookview.BookView;
import com.reader.book.manager.BookPosition;
import com.reader.book.model.Element;
import com.reader.book.model.ParagraphElement;
import com.reader.config.PageConfig;

public class Line extends AreaDraw {
	private char str[];

	public Line(BookPosition pos) {

	}

	@Override
	public float getWidth() {
		return BookView.Instance.getWidth();
	}

	@Override
	public float getHeight() {
		return BookView.getTextHeight(PageConfig.pagePaintFromConfig(false))
				+ PageConfig.getPadding();
	}

	@Override
	public void draw(Canvas canvas, float left, float top, Paint paint) {
		canvas.drawText(new String(str), left, top, paint);
	}

	@Override
	public void fill() {
		// TODO Auto-generated method stub
		final int width = BookView.Instance.getWidth();
		final Paint paint = PageConfig.pagePaintFromConfig(false);
		List<Line> mLines = new ArrayList<Line>();
		float linewidth = 0;
		if (element instanceof ParagraphElement) {
			final ParagraphElement para = (ParagraphElement)element;
			while(linewidth < width) {
				para.charAt(offset + lenght);
			}
		}
		
		// if the element only had a char '\n',should return

	}
}
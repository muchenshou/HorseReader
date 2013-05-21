package com.reader.book;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.reader.book.bookview.BookView;
import com.reader.book.manager.BookPosition;
import com.reader.book.model.Element;
import com.reader.book.model.ParagraphElement;
import com.reader.config.PageConfig;

public class Line extends AreaDraw {
	private char str[];

	public Line(BookPosition pos, Element element) {
		this.offset = pos.mOffset;
		this.element = element;
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
		Log.i("hello", "line file");
		final int width = BookView.Instance.getWidth();
		final Paint paint = PageConfig.pagePaintFromConfig(false);
		char chars[] = new char[1];
		float widths[] = new float[1];
		float linewidth = 0.0f;
		if (element instanceof ParagraphElement) {

			final ParagraphElement para = (ParagraphElement) element;
			Log.i("hello", "line file1:" + para.toString());
			while (linewidth < width && (offset + lenght) < element.getLength()) {
				chars[0] = para.charAt(offset + lenght);
				paint.getTextWidths(chars, 0, 1, widths);
				linewidth += widths[0];
				Log.i("hello", "width:" + linewidth + "offset:" + offset
						+ "lenght" + lenght);
				lenght++;
			}
			str = new char[lenght];
			para.copy(str, offset, lenght);
		}

		// if the element only had a char '\n',should return

	}
}
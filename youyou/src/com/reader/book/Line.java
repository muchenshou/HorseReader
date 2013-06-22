package com.reader.book;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.reader.book.manager.BookManager;
import com.reader.book.model.MarkupElement;
import com.reader.book.model.ParagraphElement;
import com.reader.book.model.UmdParagraphElement;
import com.reader.config.PageConfig;

public class Line extends AreaDraw {
	private char str[];

	public Line(int offset, MarkupElement element) {
		this.offset = offset;
		this.element = element;
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
		canvas.drawText(new String(str), getX(), getY() - PageConfig.getBaseLineText(), paint);
	}

	@Override
	public void fill() {
		final int width = BookManager.View.getWidth() - 15;
		final Paint paint = PageConfig.pagePaintFromConfig(false);
		char chars[] = new char[1];
		float widths[] = new float[1];
		float linewidth = 0.0f;
		if (element instanceof ParagraphElement
				|| element instanceof UmdParagraphElement) {

			final ParagraphElement para = (ParagraphElement) element;
			while ((offset + length) < element.getLength()) {
				chars[0] = para.charAt(offset + length);
				paint.getTextWidths(chars, 0, 1, widths);
				if ((linewidth + widths[0]) < width) {
					linewidth += widths[0];
					length++;
				} else {
					break;
				}
			}
			str = new char[length];
			para.copy(str, offset, length);
			length = str.length;
		}
		// if the element only had a char '\n',should return
	}
}
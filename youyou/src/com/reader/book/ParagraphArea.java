package com.reader.book;

import com.reader.book.model.ParagraphElement;
import com.reader.config.PageConfig;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.sax.Element;

public class ParagraphArea extends AreaDraw {
	private ParagraphElement _element;
	private float _height;
	private float _width;

	public ParagraphArea(ParagraphElement ele) {
		_element = ele;
	}

	@Override
	public float getWidth() {
		// TODO Auto-generated method stub
		return _width;
	}

	@Override
	public float getHeight() {
		return _height;
	}

	@Override
	public void draw(Canvas canvas, float left, float top, Paint paint) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fill() {
		final Paint paint = PageConfig.pagePaintFromConfig(false);
		char chars[] = _element.content;
		float widths[] = new float[chars.length];
		paint.getTextWidths(chars, 0, chars.length, widths);
		_height = 0f;
		for (float f : widths) {
			_height += f;
		}
		// retrieved font height
		_width = PageConfig.getTextHeight(paint);
	}

}

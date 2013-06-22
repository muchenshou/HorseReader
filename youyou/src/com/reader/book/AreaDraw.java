package com.reader.book;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import com.reader.book.model.MarkupElement;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class AreaDraw {
	public MarkupElement element;
	public int offset;
	public int length;
	LinkedList<AreaDraw> mLines = new LinkedList<AreaDraw>();

	public abstract float getWidth();

	public abstract float getHeight();

	public abstract void draw(Canvas canvas, float left, float top, Paint paint);

	public abstract void fill();

	public float getX() {
		return PosX;
	}

	public float getY() {
		return PosY;
	}

	private float PosX, PosY;

	public void setPos(float x, float y) {
		PosX = x;
		PosY = y;
	}

	public void add(AreaDraw area) {
		throw new UnsupportedOperationException();
	}

	public boolean hasChild() {
		return false;
	}

	public Iterator<AreaDraw> createIterator() {
		throw new UnsupportedOperationException();
	}

}

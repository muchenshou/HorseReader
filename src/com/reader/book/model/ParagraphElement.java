package com.reader.book.model;

import java.util.ArrayList;

import com.reader.book.Book;
import com.reader.book.CharInfo;

public class ParagraphElement extends Element {
	Cursor mStart;
	Cursor mLast;
	ArrayList<Character> content = new ArrayList<Character>();
	Book mBook;
	public ParagraphElement(Book book) {
		mBook = book;
	}
	public void fill() {
		Cursor c = new Cursor(mStart);
		CharInfo ch;
		while (c.mPosition<mLast.mPosition) {
			ch = mBook.getChar(c.mPosition);
			c.mPosition += ch.length;
			content.add(ch.character);
		}
		
	}
	public Element next() {
		return null;
	}
	
	public static Element findByCursor(Book book, Cursor c) {
		Cursor paraStart;
		Cursor paraLast;
		Cursor cursor = new Cursor(c);
		CharInfo ch = book.getChar(--cursor.mPosition);
		while (!(cursor.mPosition == 0 || ch.character == '\n')) {
			cursor.mPosition--;
			ch = book.getChar(cursor.mPosition);
		}
		cursor.mPosition += ch.length;
		paraStart = cursor;
		
		cursor.mPosition = c.mPosition;
		
		ch = book.getChar(cursor.mPosition);
		while (!(cursor.mPosition == book.size()-1 || ch.character == '\n')) {
			cursor.mPosition += ch.length;
			ch = book.getChar(cursor.mPosition);
		}
		cursor.mPosition -= ch.length;
		paraLast = cursor;
		
		// judge which type of element
		// now is text element and may be type image in the future
		ParagraphElement element = new ParagraphElement(book);
		element.mStart = paraStart;
		element.mLast = paraLast;
		element.fill();
		return element;
	}
}

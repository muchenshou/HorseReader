/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book;

import java.io.File;
import java.io.IOException;

import com.reader.book.text.TextBook;
import com.reader.book.umd.UmdBook;

public class BookFactory {
	public static Book createBook(File file) throws IOException {
		String str = file.toString()
				.substring(file.toString().lastIndexOf('.') + 1).toLowerCase();
		if (str.equals("umd")) {
			return new UmdBook(file);
		}
		if (str.equals("txt")) {
			return new TextBook(file);
		}
		return null;

	}
}

package com.Reader.Book;

import java.io.File;
import java.io.IOException;

import com.Reader.Book.Text.TextBook;
import com.Reader.Book.Umd.UmdBook;

public class BookFactory {
	public static Book createBook(File file) throws IOException {
		String str = file.toString().substring(
				file.toString().lastIndexOf('.') + 1).toLowerCase();
		if (str.equals("umd")) {
			return  new UmdBook(file);
		}
		if (str.equals("txt")){
			return (Book) new TextBook(file);
		}
		return null;

	}
}

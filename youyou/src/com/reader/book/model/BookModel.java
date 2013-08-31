package com.reader.book.model;

import com.reader.book.AreaDraw;
import com.reader.book.Book;
import com.reader.book.Page;
import com.reader.code.text.GBKTextReader;

public class BookModel {
	public Book mBook;

	public BookModel(Book book) {
		mBook = book;
		reader = new GBKTextReader(book.inputStream());
		// -----------------------
		// ��Ҫ������
		// reader.close()
		// -----------------------
	}

	GBKTextReader reader;
	// ÿһҳ��λ�ô洢������ ///
	/*
	 * ���ڱ�ʾ�鼮λ�õ�������int�ͣ�
	 * �����϶�����ݲ�ͬ��ʽ���в�ͬ�ı�ʾ����
	 * */
	int pagePos[];

	public Page getPage(int index) {
		/*
		 * ��һ����ʵ��Ҫ�Ǹ���reader��ȡÿһ��MarkupElement��
		 * MarkupElement ����DrawArea��������DrawArea����Ĵ�С
		 * ������Ӧ��DrawAreaλ�ã�����ÿһҳ����ʼλ�÷���pagePos��
		 * */
		reader = new GBKTextReader(mBook.inputStream());
		Page page = new Page();
		while (reader.hasNext()) {
			AreaDraw area = reader.next().toDrawArea();
		}
		return page;
	}
}

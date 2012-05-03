package com.Reader.Record;

public class RecordBookList extends RecordFile{
	public RecordBookList(){
		super("/data/data/com.Reader.Main/booklist.txt");
	}
	public String getFileName(){
		return "/data/data/com.Reader.Main/booklist.txt";
	}
}

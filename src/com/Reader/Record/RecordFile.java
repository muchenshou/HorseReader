package com.Reader.Record;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

public class RecordFile {
	private String mFileName;
	private LinkedList<String> list = new LinkedList<String>();
	RecordFile(String strName){
		mFileName = strName;
		File history = new File(mFileName);
		try {
			//
			history.createNewFile();
			InputStream in = new BufferedInputStream(new FileInputStream(
					history));

			BufferedReader bufin = new BufferedReader(new InputStreamReader(in,
					"utf-8"));
			String strHistoryItem = null;
			while ((strHistoryItem = bufin.readLine()) != null) {
				list.add(strHistoryItem);
			}
			bufin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public List<String> getRecord() {		
		return list;
	}
	public boolean isHaveRecord(String bookname){
		return list.indexOf(bookname) == -1 ? false : true;
	}
	public void newRecord(String record){
		list.add(record);
	}
	public void deleteRecord(String record){
		int index = list.indexOf(record);
		if (index != -1){
			list.remove(index);
		}
	}
	
	public void deleteRecord(int index){
		if (index >=0 &&
				index < list.size()){
			list.remove(index);
		}
	}
	public void setRecords(LinkedList<String> li){
		list = li;
	}
	public void writeFile(){

		if (list != null) {
			try {
				OutputStream out;
				out = new BufferedOutputStream(new FileOutputStream(mFileName));

				BufferedWriter bufout = new BufferedWriter(
						new OutputStreamWriter(out, "utf-8"));
				for (int i = 0; i < list.size(); i++) {
					bufout.write(list.get(i));
					bufout.newLine();
				}
				bufout.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

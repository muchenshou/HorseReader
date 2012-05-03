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

public class RecordHistory {
	public RecordHistory(){
		RecordFile("/data/data/com.Reader.Main/history.txt");
	}

	private String mFileName;
	private LinkedList<String> list = new LinkedList<String>();
	private LinkedList<String> locallist = new LinkedList<String>();
	private void RecordFile(String strName){
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
				locallist.add(bufin.readLine());
			}
			bufin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isHaveRecord(String bookname){
		return list.indexOf(bookname) == -1 ? false : true;
	}
	
	public void addFirst(String bookname,String local){
		
		if(getRecordIndex(bookname) == -1 ||
				list.size() == 0){
			list.addFirst(bookname);
			locallist.addFirst(local);
		}else{
			setRecordFirst(getRecordIndex(bookname));
		}
		
		
	}
	public void deleteRecord(String record){
		int index = list.indexOf(record);
		deleteRecord(index);
	}
	
	public void deleteRecord(int index){
		if (index >=0 &&
				index < list.size()){
			list.remove(index);
			locallist.remove(index);
		}
	}
	public void setRecordFirst(int index){
		list.addFirst(list.get(index));
		locallist.addFirst(locallist.get(index));
		deleteRecord(index+1);
	}

	public int getRecordIndex(String record){
		return list.indexOf(record);
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
					bufout.write(locallist.get(i));
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
	public String getPosition(int index){
		return locallist.get(index);
	}
	public String getFileName() {
		return mFileName;
	}
	public void setFirstRecordPosition(int position){
		locallist.set(0, ""+position);
	}
}

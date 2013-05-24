/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.umd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.reader.book.umd.UmdInfo.Chapter;
import com.reader.util.BytesTransfer;

public class UmdParse {

	public static int UMDFLAG = 0x899b9ade;
	public static final int ISTEXT = 1;
	public static final int ISIMAGE = 0;
	public static final int BLOCKSIZE = 32768;

	public static final int TEXT_OR_IMAGE = 1;
	public static final int NAME = 2;
	public static final int AUTHOR = 3;
	public static final int YEAR = 4;
	public static final int MONTH = 5;
	public static final int DAY = 6;
	public static final int GENDOR = 7;
	public static final int PUBLISHER = 8;
	public static final int VENDOR = 9;
	public static final int SIZE = 0x0B;
	public static final int CHAPTERS = 0x83;
	public static final int CHAPTERTITLES = 0x84;
	public static final int CHAPTERCONTENT = 0x85;
	public static final int VERIFY1 = 0x0A;
	public static final int VERIFY2 = 0xF1;
	public static final int COVER = 0x82;
	public static final int END = 0x0C;
	protected int bookNumChapters;
	private UmdInfo umdinfo;
	private RandomAccessFile mReadFile;

	public UmdParse(File file, String per) throws FileNotFoundException {
		mReadFile = new RandomAccessFile(file, per);
	}

	private int getInt() throws IOException {
		byte[] buf = new byte[4];
		mReadFile.read(buf);
		return BytesTransfer.toInt(buf);
	}

	private short getShort() throws IOException {
		byte[] buf = new byte[2];
		mReadFile.read(buf);
		return BytesTransfer.toShort(buf);
	}

	private int getSeperator() throws IOException {// #ºÅ
		return mReadFile.readByte();
	}

	private byte[] getInfo() throws IOException {
		byte numOfByte[] = new byte[2];
		mReadFile.read(numOfByte);
		byte tmp[] = new byte[numOfByte[1] - 5];
		System.out.println("this is read " + tmp.length);
		mReadFile.read(tmp);
		return tmp;
	}

	private int processWithJing() throws IOException {
		int dataType;
		int parsingType = -1;
		dataType = getShort();
		byte[] tmp = getInfo();
		switch (dataType) {
		case UmdParse.TEXT_OR_IMAGE: {
			if (UmdParse.ISTEXT == tmp[0]) {
				System.out.println("this is text!");
			}
			break;
		}
		case UmdParse.NAME:
		case UmdParse.AUTHOR:
		case UmdParse.YEAR:
		case UmdParse.MONTH:
		case UmdParse.DAY:
		case UmdParse.GENDOR:
		case UmdParse.PUBLISHER:
		case UmdParse.VENDOR: {
			BytesTransfer.byteAlign(tmp);
			umdinfo.bookInfo.put(dataType, new String(tmp, "Unicode"));
			break;
		}
		case UmdParse.SIZE: {
			umdinfo.bookSize = BytesTransfer.toInt(tmp);
			break;
		}
		case UmdParse.CHAPTERS: {
			parsingType = dataType;
			break;
		}
		case UmdParse.CHAPTERTITLES: {
			parsingType = dataType;
			break;
		}
		case UmdParse.VERIFY1:
		case UmdParse.VERIFY2:
			break;
		case UmdParse.COVER:
			parsingType = UmdParse.COVER;
			break;
		case UmdParse.END:
			parsingType = END;
			break;
		}
		return parsingType;
	}

	private int processWithMoney(int parsingType) throws IOException {

		if (parsingType == CHAPTERS) {
			int num;
			mReadFile.skipBytes(4);
			num = getInt();
			bookNumChapters = (num - 9) / 4;
			int chapters;
			for (int i = 0; i < bookNumChapters; i++) {
				chapters = getInt();
				Chapter cha = umdinfo.new Chapter();
				cha.setChapterStartLocal(chapters);
				cha.setChapterNo(i);
				umdinfo.chapterList.add(cha);
			}
		} else if (parsingType == CHAPTERTITLES) {
			mReadFile.skipBytes(4);
			getInt();
			for (int i = 0; i < bookNumChapters; i++) {
				byte count = mReadFile.readByte();
				byte[] title = new byte[count];
				mReadFile.read(title);
				BytesTransfer.byteAlign(title);

				String str = new String(title, "Unicode");
				Chapter cha = umdinfo.chapterList.get(i);
				cha.setChapterName(str);
			}
			parsingType = CHAPTERCONTENT;
		} else if (parsingType == CHAPTERCONTENT) {

			mReadFile.skipBytes(4);
			int num = getInt();
			umdinfo.blockList.add(umdinfo.new Block(mReadFile.getFilePointer(),
					num - 9));
			mReadFile.skipBytes(num - 9);

		} else if (parsingType == COVER) {
			mReadFile.skipBytes(4);
			int num = getInt();
			mReadFile.skipBytes(num - 9);
		}
		return parsingType;
	}

	public UmdInfo parseBook() throws IOException {
		umdinfo = new UmdInfo();
		int parsingType = 0;
		if (mReadFile.readInt() != UmdParse.UMDFLAG) {
			System.out.println("the book is not umd format!");
		} else {
			System.out.println("the book is umd format!");
		}
		int pound = getSeperator();
		while (pound == '#' || pound == '$') {
			if (pound == '#') {
				parsingType = processWithJing();
			} else if (pound == '$') {
				parsingType = processWithMoney(parsingType);
			} else {
				throw new RuntimeException("failed to parse umd");
			}
			if (parsingType == END) {
				mReadFile.close();
				break;
			}
			pound = mReadFile.readByte();
		}
		return umdinfo;
	}
}

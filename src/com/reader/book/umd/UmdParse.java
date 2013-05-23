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

import android.util.Log;

import com.reader.book.umd.UmdInfo.Block;
import com.reader.book.umd.UmdInfo.Chapter;
import com.reader.util.BytesTransfer;

public class UmdParse extends RandomAccessFile {

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
	protected int bookNumChapters;
	private UmdInfo umdinfo;

	public UmdParse(File file, String per) throws FileNotFoundException {
		super(file, per);
	}

	public int getInt() throws IOException {
		byte[] buf = new byte[4];
		this.read(buf);
		return BytesTransfer.toInt(buf);
	}

	public short getShort() throws IOException {
		byte[] buf = new byte[2];
		this.read(buf);
		return BytesTransfer.toShort(buf);
	}

	public int getSeperator() throws IOException {// #��
		return this.readByte();
	}

	public short getDataType() throws IOException {
		return this.getShort();
	}

	public byte[] getInfo() throws IOException {
		byte numOfByte[] = new byte[2];
		this.read(numOfByte);
		byte tmp[] = new byte[numOfByte[1] - 5];
		System.out.println("this is read " + tmp.length);
		this.read(tmp);
		return tmp;
	}

	private boolean parseChapters() {
		try {
			int num;
			byte partition = readByte();
			if (partition == (byte) '$') {
				skipBytes(4);
				num = getInt();
				bookNumChapters = (num - 9) / 4;
				System.out.println("parseing: the number of the book "
						+ bookNumChapters);
				int chapters;
				for (int i = 0; i < bookNumChapters; i++) {
					chapters = getInt();
					// System.out.println("the offset is " + i + "chapter"
					// + chapters);
					Chapter cha = umdinfo.new Chapter();
					cha.setChapterStartLocal(chapters);
					umdinfo.chapterList.add(cha);
				}
			}
		} catch (IOException io) {
			System.out.println(io);
		}

		return true;
	}

	public boolean parseChapterContent() {
		try {
			int num;
			byte partition = readByte();
			while (partition == (byte) '$') {
				skipBytes(4);
				num = getInt();
				umdinfo.blockList.add(umdinfo.new Block(getFilePointer(),
						num - 9));

				skipBytes(num - 9);

				partition = readByte();
				while (partition == (byte) '#') {
					// System.out.println("block here");
					short flag = getShort();
					if (flag == (short) 0x0A) {
						skipBytes(6);

						// System.out.println("block here 0x0a");
					}
					if (flag == (short) 0xF1) {
						Log.i("hello", "0xF1");
						skipBytes(18);

						// System.out.println("block here 0xf1");
					}
					partition = readByte();
				}
			}
		} catch (IOException io) {
			System.out.println(io);
		}
		return true;
	}

	public boolean parseChaptersTitle() {
		try {

			byte partition = readByte();
			if (partition == (byte) '$') {
				skipBytes(4);
				getInt();
				// System.out.println("parseing: the length of the title " +
				// (num));
				for (int i = 0; i < bookNumChapters; i++) {
					byte count = readByte();
					byte[] title = new byte[count];
					read(title);
					BytesTransfer.byteAlign(title);

					String str = new String(title, "Unicode");
					Chapter cha = umdinfo.chapterList.get(i);
					cha.setChapterName(str);
					// System.out.println("the " + i + " chapter title is " +
					// str);
				}
				parseChapterContent();
			}
		} catch (IOException io) {
			System.out.println(io);
		}
		return true;
	}

	public UmdInfo parseBook() throws IOException {
		umdinfo = new UmdInfo();
		try {
			int dataType;

			if (readInt() != UmdParse.UMDFLAG) {
				System.out.println("the book is not umd format!");
			} else {
				System.out.println("the book is umd format!");
			}
			int pound = getSeperator();
			while (pound == '#') {
				System.out.println("entrance!");
				dataType = getShort();

				System.out.println("this is " + dataType);

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
					parseChapters();
					break;
				}
				case UmdParse.CHAPTERTITLES: {
					parseChaptersTitle();
					break;
				}
				}
				pound = readByte();

			}
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
		int size = umdinfo.blockList.size();
		for (int i = 0; i < size; i++) {
			Block block = umdinfo.blockList.get(i);
			block.setBlockNo(i);
		}
		size = umdinfo.chapterList.size();
		for (int i = 0; i < size; i++) {
			Chapter cha = umdinfo.chapterList.get(i);
			cha.setChapterNo(i);
		}
		return umdinfo;
	}

	public int getPointerInWhichBlock(int pointer) {
		return pointer / (UmdParse.BLOCKSIZE);
	}

}

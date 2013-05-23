/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.umd;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import android.util.SparseArray;

public class UmdInfo {

	protected UmdParse umdStream;
	protected int bookSize;

	protected LinkedList<Block> blockList;
	protected LinkedList<Chapter> chapterList;
	protected SparseArray<String> bookInfo;

	public UmdInfo() {

		blockList = new LinkedList<Block>();

		chapterList = new LinkedList<Chapter>();
		bookInfo = new SparseArray<String>();
	}

	public String getName() {
		return bookInfo.get(UmdParse.NAME);
	}

	public String getAuthor() {
		return bookInfo.get(UmdParse.AUTHOR);
	}

	public String getYear() {
		return bookInfo.get(UmdParse.YEAR);
	}

	public String getMonth() {
		return bookInfo.get(UmdParse.MONTH);
	}

	public String getDay() {
		return bookInfo.get(UmdParse.DAY);
	}

	public String getGendor() {
		return bookInfo.get(UmdParse.GENDOR);
	}

	public String getPublisher() {
		return bookInfo.get(UmdParse.PUBLISHER);
	}

	public String getVendor() {
		return bookInfo.get(UmdParse.VENDOR);
	}

	public int getSize() {
		return bookSize;
	}

	public int getPointerInBlockLocal(int pointer) {
		return pointer % (UmdParse.BLOCKSIZE);
	}

	public Chapter getChapter(int i) throws IOException {
		return chapterList.get(i);
	}

	public Block getBlock(int index) {
		if (index < blockList.size())
			return blockList.get(index);
		return null;
	}

	public byte[] getBlockData(File umdFile, int index) throws IOException {
		byte bytes[] = null;
		try {
			umdStream = new UmdParse(umdFile, "r");
			Block b = blockList.get(index);
			this.umdStream.seek(b.filePointer);
			bytes = new byte[b.blockSize];
			umdStream.read(bytes);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			umdStream.close();
			umdStream = null;
		}
		return bytes;
	}

	public long getBlockPointer(int i) {
		return this.blockList.get(i).getPointer();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("blocksize:" + this.blockList.size());
		buffer.append('\n');
		return buffer.toString();
	}

	public class Block {
		protected int blockNo;
		protected long filePointer;
		protected int blockSize;

		public Block(long filepointer, int size) {
			filePointer = filepointer;
			blockSize = size;
		}

		public void setBlockNo(int num) {
			blockNo = num;
		}

		public long getPointer() {
			return filePointer;
		}
	}

	public class Chapter {
		protected int chapterNo;
		protected String chapterName;
		protected int chapterSize;
		protected long chapterStartLocal;

		public Chapter() {
			chapterStartLocal = 0;
			chapterNo = 0;
			chapterSize = 0;
			chapterName = null;
		}

		public Chapter(int numberOfChapter, String nameOfChapter,
				int sizeOfChapter, long startLocalOfChapter) {
			chapterNo = numberOfChapter;
			chapterName = nameOfChapter;
			chapterSize = sizeOfChapter;
			chapterStartLocal = startLocalOfChapter;
		}

		public void setChapterNo(int numberOfChapter) {
			chapterNo = numberOfChapter;
		}

		public void setChapterName(String nameOfChapter) {
			chapterName = nameOfChapter;
		}

		public void setChapterSize(int sizeOfChapter) {
			chapterSize = sizeOfChapter;
		}

		public void setChapterStartLocal(long startLocalOfChapter) {
			chapterStartLocal = startLocalOfChapter;
		}
	}
}

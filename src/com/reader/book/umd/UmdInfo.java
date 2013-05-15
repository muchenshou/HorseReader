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

import android.util.Log;
import android.util.SparseArray;

import com.reader.util.BytesTransfer;

public class UmdInfo {

	protected UmdParse umdStream;
	private File umdFile;
	protected int bookSize;
	protected int bookNumChapters;
	protected LinkedList<Block> blockList;
	protected LinkedList<Chapter> chapterList;
	protected SparseArray<String> bookInfo;

	public UmdInfo(File umd) {

		umdFile = umd;
		blockList = new LinkedList<Block>();

		chapterList = new LinkedList<Chapter>();
		bookInfo = new SparseArray<String>();
	}

	public File getFile() {
		return umdFile;
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

	public int getBookNumChapters() {
		return bookNumChapters;
	}

	private boolean parseChapters() {
		try {
			int num;
			byte partition = umdStream.readByte();
			if (partition == (byte) '$') {
				umdStream.skipBytes(4);
				num = umdStream.getInt();
				bookNumChapters = (num - 9) / 4;
				System.out.println("parseing: the number of the book "
						+ bookNumChapters);
				int chapters;
				for (int i = 0; i < bookNumChapters; i++) {
					chapters = umdStream.getInt();
					// System.out.println("the offset is " + i + "chapter"
					// + chapters);
					Chapter cha = new Chapter();
					cha.setChapterStartLocal(chapters);
					this.chapterList.add(cha);
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
			byte partition = umdStream.readByte();
			while (partition == (byte) '$') {
				umdStream.skipBytes(4);
				num = umdStream.getInt();
				this.blockList.add(new Block(umdStream.getFilePointer(),
						num - 9));

				umdStream.skipBytes(num - 9);

				partition = umdStream.readByte();
				while (partition == (byte) '#') {
					// System.out.println("block here");
					short flag = umdStream.getShort();
					if (flag == (short) 0x0A) {
						umdStream.skipBytes(6);
						
						// System.out.println("block here 0x0a");
					}
					if (flag == (short) 0xF1) {
						Log.i("hello", "0xF1");
						umdStream.skipBytes(18);

						// System.out.println("block here 0xf1");
					}
					partition = umdStream.readByte();
				}
			}
		} catch (IOException io) {
			System.out.println(io);
		}
		return true;
	}

	public boolean parseChaptersTitle() {
		try {

			byte partition = umdStream.readByte();
			if (partition == (byte) '$') {
				umdStream.skipBytes(4);
				umdStream.getInt();
				// System.out.println("parseing: the length of the title " +
				// (num));
				for (int i = 0; i < bookNumChapters; i++) {
					byte count = umdStream.readByte();
					byte[] title = new byte[count];
					umdStream.read(title);
					BytesTransfer.byteAlign(title);

					String str = new String(title, "Unicode");
					Chapter cha = this.chapterList.get(i);
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

	public boolean parseBook() throws IOException {
		umdStream = new UmdParse(this.umdFile, "r");
		try {
			int dataType;

			if (umdStream.readInt() != UmdParse.UMDFLAG) {
				System.out.println("the book is not umd format!");
			} else {
				System.out.println("the book is umd format!");
			}
			int pound = umdStream.getSeperator();
			while (pound == '#') {
				System.out.println("entrance!");
				dataType = umdStream.getShort();

				System.out.println("this is " + dataType);

				byte[] tmp = umdStream.getInfo();

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
					bookInfo.put(dataType, new String(tmp, "Unicode"));
					break;
				}
				case UmdParse.SIZE: {
					bookSize = BytesTransfer.toInt(tmp);
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
				pound = umdStream.readByte();

			}
		} catch (IOException e) {
			System.out.println(e);
			return false;
		} finally {
			umdStream.close();
			umdStream = null;
		}
		int size = this.blockList.size();
		for (int i = 0; i < size; i++) {
			Block block = blockList.get(i);
			block.setBlockNo(i);
		}
		size = this.chapterList.size();
		for (int i = 0; i < size; i++) {
			Chapter cha = chapterList.get(i);
			cha.setChapterNo(i);
		}
		return true;
	}

	public int getPointerInWhichBlock(int pointer) {
		return pointer / (UmdParse.BLOCKSIZE);
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

	public byte[] getBlockData(int index) throws IOException {
		byte bytes[] = null;
		try {
			umdStream = new UmdParse(this.umdFile, "r");
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

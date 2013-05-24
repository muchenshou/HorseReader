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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;
import com.reader.book.Book;
import com.reader.book.BookBuffer;
import com.reader.book.CharInfo;

public class UmdBook extends Book {
	public UmdInfo umdInfo = null;
	UmdInflate umdinflate;
	private BookBuffer bookBuffer = new BookBuffer(this);

	public UmdBook(File umd) throws IOException {
		bookFile = umd;
		umdInfo = new UmdInfo();

		UmdParse umdStream = new UmdParse(bookFile, "r");
		umdInfo = umdStream.parseBook();
		umdinflate = new UmdInflate();
	}

	public File getFile() {
		return bookFile;
	}

	@Override
	public String getName() {
		return umdInfo.getName();
	}

	@Override
	public int getContent(int start, ByteBuffer contentBuffer) {
		int length = contentBuffer.capacity();
		byte[] content;
		try {
			if (this.getPointerInWhichBlock(start) == this
					.getPointerInWhichBlock(start + length - 1)) {

				content = umdinflate.getContentBlock(
						this.getPointerInWhichBlock(start),
						this.getPointerInBlockLocal(start), length);
				contentBuffer.put(content);
			} else {
				content = umdinflate.getContentBlock(
						this.getPointerInWhichBlock(start),
						this.getPointerInBlockLocal(start), UmdParse.BLOCKSIZE
								- this.getPointerInBlockLocal(start));
				if (content == null) {
					System.out.println(" Is null\t"
							+ this.getPointerInWhichBlock(start) + "\t"
							+ this.getPointerInBlockLocal(start) + "\t"
							+ UmdParse.BLOCKSIZE);
				}
				contentBuffer.put(content);
				for (int i = this.getPointerInWhichBlock(start) + 1; i < this
						.getPointerInWhichBlock(start + length - 1); i++) {
					content = umdinflate.getContentBlock(
							this.getPointerInWhichBlock(start), 0,
							UmdParse.BLOCKSIZE);
					contentBuffer.put(content);
				}
				content = umdinflate.getContentBlock(
						this.getPointerInWhichBlock(start + length - 1),
						0,
						UmdParse.BLOCKSIZE
								- this.getPointerInBlockLocal(start + length
										- 1));
				contentBuffer.put(content);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.EOFBOOK = true;
			return -1;
		}
		ByteOrder order = ByteOrder.LITTLE_ENDIAN;
		contentBuffer.order(order);
		return contentBuffer.limit();
	}

	public int getPointerInWhichBlock(int pointer) {
		return pointer / (UmdParse.BLOCKSIZE);
	}

	public int getPointerInBlockLocal(int pointer) {
		return pointer % (UmdParse.BLOCKSIZE);
	}

	public int getChapterLocal(int num) {
		if (num < 0)
			return -1;
		return (int) this.umdInfo.chapterList.get(num).chapterStartLocal;
	}

	public int localIsInWhichChapter(int local) {
		int chapterNum = 0;
		for (; chapterNum < umdInfo.chapterList.size(); chapterNum++) {
			if (local < umdInfo.chapterList.get(chapterNum).chapterStartLocal) {
				return chapterNum--;
			}
		}
		return -1;
	}

	public int blockIndex = -1;
	public byte[] blockDataBuffer = null;

	public byte[] getBlockData(int index) throws IOException {
		if (index == blockIndex) {
			return blockDataBuffer;
		}
		byte bytes[] = umdInfo.getBlockData(bookFile, index);
		blockDataBuffer = bytes;
		blockIndex = index;
		return bytes;
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

	@Override
	public void openBook() {

		// try {
		// this.umdStream = new UmdParse(this.bookFile, "r");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public void closeBook() {
		// if (umdStream != null) {
		// try {
		// umdStream.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
	}

	@Override
	public void excuteCmd(int cmd) {

	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.umdInfo.getSize();
	}

	@Override
	public CharInfo getChar(int pos) {
		// if(mEnd > boo)
		if (pos >= this.size())
			return null;
		CharInfo charinfo = new CharInfo();
		charinfo.character = this.bookBuffer.getChar(pos);
		if (charinfo.character == 8233)
			charinfo.character = '\n';
		charinfo.length = 2;
		charinfo.position = pos;
		return charinfo;
	}

	@Override
	public CharInfo getPreChar(int start) {
		return getChar(start - 2);
	}

	@SuppressWarnings("deprecation")
	class UmdInflate {
		public byte[] Inflate(byte[] content) {
			int err;
			int uncomprLen = 40000;
			byte[] uncompr;
			int comprLen = content.length;
			uncompr = new byte[uncomprLen];

			Inflater inflater = new Inflater();

			inflater.setInput(content);
			inflater.setOutput(uncompr);

			err = inflater.init();
			CHECK_ERR(inflater, err, "inflateInit");

			while (inflater.total_out < uncomprLen
					&& inflater.total_in < comprLen) {
				inflater.avail_in = inflater.avail_out = 1; /*
															 * force small
															 * buffers
															 */
				err = inflater.inflate(JZlib.Z_NO_FLUSH);
				if (err == JZlib.Z_STREAM_END)
					break;
				CHECK_ERR(inflater, err, "inflate");
			}

			err = inflater.end();
			CHECK_ERR(inflater, err, "inflateEnd");
			return uncompr;
		}

		public byte[] getContentBlock(int index, int start, int length)
				throws IOException {
			byte[] content = null;

			int err;
			content = new byte[length];
			byte[] in = getBlockData(index);
			Inflater inflater = new Inflater();

			inflater.setInput(in);
			inflater.setOutput(content);

			while (inflater.total_in < in.length) {
				inflater.avail_in = inflater.avail_out = 1; /*
															 * force small
															 * buffers
															 */
				if (inflater.total_out <= start) {
					inflater.next_out_index = 0;
				}
				if (inflater.total_out > start + length - 1)
					break;
				err = inflater.inflate(JZlib.Z_NO_FLUSH);
				if (err == JZlib.Z_STREAM_END) {
					System.out.println("z-stream-end");
					break;
				}
				CHECK_ERR(inflater, err, "inflate2");
			}

			err = inflater.end();
			CHECK_ERR(inflater, err, "inflateEnd");

			return content;
		}

		void CHECK_ERR(Inflater z, int err, String msg) {
			if (err != JZlib.Z_OK) {
				if (z.msg != null)
					System.out.print(z.msg + " ");
				System.out.println(msg + " error: " + err);

				System.exit(1);
			}
		}
	}
}

/*
 * QQ:1127082711
 * 
 * xinlangweibo:http://weibo.com/muchenshou
 * 
 * email:muchenshou@gmail.com
 * */
package com.reader.book.umd;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import com.reader.book.Book;
import com.reader.book.BookBuffer;
import com.reader.book.CharInfo;

public class UmdBook extends Book {
	protected UmdParse umdStream;
	public UmdInfo umdInfo = null;
	UmdInflate umdinflate;
	private BookBuffer bookBuffer = new BookBuffer(this);

	public UmdBook(File umd) throws IOException {
		bookFile = umd;
		umdInfo = new UmdInfo(umd);
		umdInfo.parseBook();
		umdinflate = new UmdInflate(this);
	}

	public File getFile() {
		return bookFile;
	}

	@Override
	public String getName(){
		return umdInfo.getName();
	}
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

	public byte[] getBlockContent(int index) throws IOException {
		UmdInflate umdinflate = new UmdInflate(this);
		byte[] uncomp = umdinflate.Inflate(getBlockData(index));
		Byte b = new Byte(uncomp[0]);
		System.out.println("first byte:\t " + b.toString());
		BytesTransfer.byteAlign(uncomp);
		return uncomp;
	}

	public int blockIndex = -1;
	public byte[] blockDataBuffer = null;

	public byte[] getBlockData(int index) throws IOException {
		if (index == blockIndex) {
			return blockDataBuffer;
		}
		byte bytes[] = null;
		try {
			umdStream = new UmdParse(this.bookFile, "r");
			UmdInfo.Block b = this.umdInfo.getBlock(index);
			this.umdStream.seek(b.filePointer);
			bytes = new byte[b.blockSize];
			umdStream.read(bytes);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			umdStream.close();
			umdStream = null;
		}
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

	public void openBook() {

		try {
			this.umdStream = new UmdParse(this.bookFile, "r");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeBook() {
		if (umdStream != null) {
			try {
				umdStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void excuteCmd(int cmd) {

	}

	public int size() {
		// TODO Auto-generated method stub
		return this.umdInfo.getSize();

	}

	public CharInfo getChar(int pos) {
		// if(mEnd > boo)
		if (pos >= this.size())
			return null;
		CharInfo charinfo = new CharInfo();
		charinfo.character = this.bookBuffer.getChar(pos);
		if (charinfo.character==8233)
			charinfo.character = '\n';
		charinfo.length = 2;
		charinfo.position = pos;
		return charinfo;
	}

	public CharInfo getPreChar(int start) {
		return getChar(start - 2);
	}
}

package com.Reader.Book; 
 
import java.nio.ByteBuffer; 
import java.nio.ByteOrder; 
 
import android.util.Log; 
 
public class BookBuffer implements Runnable { 
    private Book mBook = null; 
    private int mBuf1Position = -1; 
    private int mBufferSize = 1024;// buffer 4k 
    private ByteBuffer mBuffer1 = ByteBuffer.allocate(mBufferSize); 
    private int mBuf1LenghtContent = -1; 
    private int mBuf2Position = -1; 
    private ByteBuffer mBuffer2 = ByteBuffer.allocate(mBufferSize); 
    private int mBuf2LenghtContent = -1; 
 
    public BookBuffer(Book book) { 
        mBook = book; 
        new Thread(this).start(); 
        
    } 
 
    boolean have(int location) { 
        // Log.i("\nhave is\t", "location:"+location 
        // +"\tmPosition:"+mPosition+"\t mLenghtContent:"+ mLenghtContent); 
        if (mBuf1Position == -1) 
            return false; 
        if (location /this.mBufferSize != this.mBuf1LenghtContent) { 
            return false; 
        } 
        return true; 
    } 
 
    public char getChar(int location) { 
        ByteBuffer charbuf = ByteBuffer.allocate(2); 
        charbuf.clear(); 
        charbuf.put(this.getByte(location)); 
        charbuf.put(this.getByte(location + 1)); 
        charbuf.flip(); 
        charbuf.order(ByteOrder.LITTLE_ENDIAN); 
        return charbuf.getChar(); 
 
    } 
 
    public byte getByte(final int location) { 
        if (location >= this.mBook.size()) 
            return 0; 
        long one = System.currentTimeMillis(); 
        if (have(location)) {  
            return mBuffer1.get(location - mBuf1Position); 
        } 
        if (haveInBuf2(location)) { 
            synchronized (this) {  
                mBuffer1.clear(); 
                ByteBuffer mid = mBuffer1; 
                mBuffer1 = mBuffer2; 
                mBuffer2 = mid; 
                mBuf1Position = mBuf2Position; 
                mBuf1LenghtContent = mBuf2LenghtContent; 
 
            } 
            synchronized (this) { 
                this.notify(); 
            } 
            
            long two = System.currentTimeMillis(); 
            Log.i("[Thread]", "" + (two - one));
            Log.i("[Thread]","location:"+location);
            Log.i("[Thread]","buf1local"+this.mBuf1Position);
            Log.i("[Thread]","buf1len"+this.mBuf1LenghtContent);
            //return mBuffer1.get(location - mBuf1Position); 
            return this.getByte(location); 
        } 
        mBuffer1.clear(); 
        Log.i("[Thread2]","location"+location);
         
        mBook.getContent((location/this.mBufferSize)*this.mBufferSize, mBuffer1);
        mBuf1LenghtContent = location/this.mBufferSize;
        this.mBuf1Position = (location/this.mBufferSize)*this.mBufferSize;
        synchronized (this) { 
            this.notify(); 
        }
        long two = System.currentTimeMillis(); 
        Log.i("[Thread2]", "" + (two - one)); 
        
        return this.getByte(location); 
    } 
 
    private boolean haveInBuf2(int location) { 
        // Log.i("\nhave is\t", "location:"+location 
        // +"\tmPosition:"+mPosition+"\t mLenghtContent:"+ mLenghtContent); 
        if (mBuf2Position == -1) 
            return false; 
        if (location/this.mBufferSize != this.mBuf2LenghtContent) { 
            return false; 
        } 
        return true; 
    } 
 
    @Override 
    public void run() { 
        while (!Thread.currentThread().isInterrupted()) { 
 
            synchronized (this) { 
                try { 
                    this.wait(); 
                } catch (InterruptedException e) { 
                    // TODO Auto-generated catch block 
                    e.printStackTrace(); 
                }
                
                mBuffer2.clear(); 
                mBook.getContent(this.mBuf1Position 
                        + this.mBufferSize, mBuffer2); 
                mBuf2LenghtContent = this.mBuf1LenghtContent+1;
                mBuf2Position = this.mBuf1Position + this.mBufferSize;
                Log.i("[Thread here]","here"+mBuf2Position);
                //this.mBuffer2.notify(); 
            } 
        } 
    } 
} 

package com.reader.searchfile;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchFileWithMultiThread extends SearchFile implements Runnable {
	AtomicInteger activeThreadCount;
	LinkedBlockingQueue<File> dirs;
	final int count = Runtime.getRuntime().availableProcessors();
	public boolean finished = true;

	ExecutorService mExecutor = Executors.newFixedThreadPool(count);

	public SearchFileWithMultiThread(FindOneBehavior findone, String rootdir) {
		super(findone, rootdir);
		activeThreadCount = new AtomicInteger(0);
		dirs = new LinkedBlockingQueue<File>();
		try {
			dirs.put(new File(rootdir));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		for (int i = 0; i < count; i++) {
			mExecutor.execute(this);
		}
		finished = false;
		System.out.println("start ok");
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		File file = null;
		while ((file = dirs.poll()) != null || activeThreadCount.get() != 0) {
			if (file == null)
				continue;
			activeThreadCount.incrementAndGet();
			File files[] = file.listFiles(mFilter);
			if (files != null)
				for (File f : files) {
					if (f.isFile())
						mFindOne.accept(f);
					else if (f.isDirectory())
						dirs.offer(f);
				}
			activeThreadCount.decrementAndGet();
		}
		finished = true;
		System.out.println("time:" + (System.currentTimeMillis() - start));
	}

	@Override
	public void search() {
		start();
		while(!finished){
			
		}
	}

	public static void main(String[] args) {
		SearchFileWithMultiThread sf = new SearchFileWithMultiThread(
				new FindOneBehavior() {
					@Override
					public boolean accept(File pathname) {
						System.out.println(pathname.getAbsolutePath());
						return false;
					}
				}, "C:\\");// GHOST
		sf.search();
	}
}

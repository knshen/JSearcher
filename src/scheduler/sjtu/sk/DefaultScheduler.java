package scheduler.sjtu.sk;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import downloader.sjtu.sk.HtmlDownloader;
import logging.sjtu.sk.Logging;
import parser.sjtu.sk.HtmlParser;
import url.manager.sjtu.sk.URL;
import url.manager.sjtu.sk.URLManager;

public class DefaultScheduler implements Runnable {
	
	private HtmlDownloader hd = null;
	private HtmlParser hp = null;
	private URLManager um = null;
	
	private int num_threads;
	private boolean isThreadPool = false;
	private int count = 0; // # of pages have visited
	private final Lock lock = new ReentrantLock(); 
	private int maxNum = 0;
	
	public static DefaultScheduler createDefaultScheduler(int num_threads, boolean isThreadPool, int maxNum) {
		return new DefaultScheduler(num_threads, isThreadPool, maxNum);
	}
	
	private DefaultScheduler(int num_threads, boolean isThreadPool, int maxNum) {
		this.num_threads = num_threads;
		this.isThreadPool = isThreadPool;
		this.maxNum = maxNum;
		
		um = new URLManager();
		hd = new HtmlDownloader();
		hp = new HtmlParser();
	}
	
	public void startCraw(List<URL> seed) {
		this.preCraw(seed, (int)(0.1 * maxNum));
		if(isThreadPool) {
			//TODO thread pool
			//ExecutorService fixedThreadPool = Executors.newFixedThreadPool(num_threads);
			//fixedThreadPool.execute(this);
		}
		else {
			// non thread pool mode
			List<Thread> workers = new ArrayList<Thread>();
			for(int i=0; i<num_threads; i++) {
				workers.add(new Thread(this));
			}
			for(Thread th : workers) 
				th.start();	
		}
	}
	
	public void run() {
		craw();
	}
	
	private void craw() {
		while(true) {
			if(count >= maxNum)
				break;
			
			URL new_url = null;
			
			lock.lock();	
			try {
				new_url = um.fetchOneURL();
			}
			finally {
				lock.unlock();
			}
			
			// no url to visit (url queue is empty!)
			if(new_url == null) {
				try {
					Thread.sleep(2000);
				} catch(InterruptedException ie) {
					ie.printStackTrace();
				}
				
				continue;
			}
				
			String html = hd.download(new_url);
			if(html == null) 
				continue;
			
			lock.lock();
			try {
				count++;
			}
			finally {
				lock.unlock();
			}
			
			Logging.log(Thread.currentThread().getName() + " visiting: " + new_url.getURLValue());
			List<URL> new_links = hp.parse(html, new_url.getURLValue());
			
			lock.lock();
			try {
				um.addURLList(new_links);
			} 
			finally {
				lock.unlock();
			}
		} // end while
		
	}
	
	
	public void preCraw(List<URL> seed, int initNum) {
		um.addURLList(seed);
		
		while(true) {
			if(count >= initNum)
				break;
			
			URL new_url = um.fetchOneURL();
			// no url to visit (url queue is empty!)
			if(new_url == null)
				break;
			String html = hd.download(new_url);
			if(html == null) 
				continue;
			
			count++;
			Logging.log("preCraw: visiting: " + new_url.getURLValue());
			List<URL> new_links = hp.parse(html, new_url.getURLValue());
			um.addURLList(new_links);
		}
	}
	
	public static void main(String[] args) {
		URL seed = new URL("https://leetcode.com/problemset/algorithms/");
		DefaultScheduler ds = DefaultScheduler.createDefaultScheduler(2, false, 20);
		ds.startCraw(Arrays.asList(seed));
	}

}

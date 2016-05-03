package scheduler.sjtu.sk;

import java.util.*;
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
	private int count = 0;
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
	
	public void run() {
		craw();
	}
	
	public void craw() {
		lock.lock();
		try {
			count++;
			//////
		}
		finally {
			lock.unlock();
		}
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
			Logging.log("visiting: " + new_url.getURLValue());
			List<URL> new_links = hp.parse(html, new_url.getURLValue());
			um.addURLList(new_links);
		}
	}
	
	public static void main(String[] args) {
		DefaultScheduler ds = DefaultScheduler.createDefaultScheduler(1, false);
		
		URL seed = new URL("https://leetcode.com/problemset/algorithms/");
		ds.craw(Arrays.asList(seed), 10);
	}

}

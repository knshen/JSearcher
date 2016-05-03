package scheduler.sjtu.sk;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.nodes.Document;

import downloader.sjtu.sk.HtmlDownloader;
import logging.sjtu.sk.Logging;
import outputer.sjtu.sk.HtmlTableOutputer;
import outputer.sjtu.sk.Outputer;
import parser.sjtu.sk.DataExtractor;
import parser.sjtu.sk.HtmlParser;
import parser.sjtu.sk.LeetcodeProblemTitleExtractor;
import url.manager.sjtu.sk.URL;
import url.manager.sjtu.sk.URLManager;

public class DefaultScheduler implements Runnable {
	
	private HtmlDownloader hd = null;
	private HtmlParser hp = null;
	private URLManager um = null;
	private Outputer out = null;
	private DataExtractor de = null;
	
	private List<String> total_data = new ArrayList<String>();
	private int num_threads;
	private boolean isThreadPool = false;
	private int count = 0; // # of pages have visited
	private final Lock lock = new ReentrantLock(); 
	private int maxNum = 0;
	
	public static DefaultScheduler createDefaultScheduler() {
		return new DefaultScheduler();
	}
	
	public final void config(Outputer out, DataExtractor de, int num_threads, boolean isThreadPool, int maxNum) {
		// config parameters
		this.out = out;
		this.de = de;
		this.num_threads = num_threads;
		this.isThreadPool = isThreadPool;
		this.maxNum = maxNum;
	}
	
	private DefaultScheduler() {
		um = new URLManager();
		hd = new HtmlDownloader();
		hp = new HtmlParser();
	}
	
	public void runTask(List<URL> seed) {
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
				
			for(Thread th : workers) {
				try {
					th.join();
				} catch(InterruptedException ie) {
					ie.printStackTrace();
				}
			}
				
			// output html file
			out.output("/home/knshen/test.html", total_data);
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
			List<String> data = de.extract(hp.getDocument()); 
			lock.lock();
			try {
				if(data != null && data.size() > 0) 
					total_data.addAll(data);
				
				um.addURLList(new_links);
			} 
			finally {
				lock.unlock();
			}
		} // end while
		
	}
	
	private void preCraw(List<URL> seed, int initNum) {
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
			// get links & extract data
			List<URL> new_links = hp.parse(html, new_url.getURLValue());
			List<String> data = de.extract(hp.getDocument());
			if(data != null && data.size() > 0) 
				total_data.addAll(data);
			
			um.addURLList(new_links);
		}
	}
	
	public static void main(String[] args) {
		URL seed = new URL("https://leetcode.com/problemset/algorithms/");
		//create scheduler instance
		DefaultScheduler ds = DefaultScheduler.createDefaultScheduler();
		// config parameters
		ds.config(new HtmlTableOutputer(), new LeetcodeProblemTitleExtractor(), 5, false, 100);
		// run tasks
		ds.runTask(Arrays.asList(seed));
	}

}

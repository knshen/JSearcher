package scheduler.sjtu.sk;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.nodes.Document;

import db.sjtu.sk.DBWriter;
import downloader.sjtu.sk.HtmlDownloader;
import logging.sjtu.sk.Logging;
import outputer.sjtu.sk.HtmlTableOutputer;
import outputer.sjtu.sk.Outputer;
import parser.sjtu.sk.DataExtractor;
import parser.sjtu.sk.HtmlParser;
import parser.sjtu.sk.LeetcodeProblemTitleExtractor;
import url.manager.sjtu.sk.URL;
import url.manager.sjtu.sk.URLManager;
import util.sjtu.sk.OperatingSystem;
import util.sjtu.sk.Util;

/**
 * Default scheduler of the Spider
 * Scheduler is the core logic to combine all the modules together
 * This scheduler supports multi-thread working
 * @author ShenKai
 *
 */
public class DefaultScheduler implements Runnable {
	public static final double factor = 0.1;
	
	private HtmlDownloader hd = null;
	private HtmlParser hp = null;
	private URLManager um = null;
	private Outputer out = null;
	private DataExtractor de = null;
	
	private List<Object> total_data = new ArrayList<Object>(); // global crawed data 
	private int num_threads; // number of threads
	private boolean isThreadPool = false;
	private int count = 0; // number of pages have visited
	private final Lock lock = new ReentrantLock(); 
	private int maxNum = 0; // max number of pages allowed
	
	public static DefaultScheduler createDefaultScheduler() {
		return new DefaultScheduler();
	}
	
	/**
	 * configure parameters of the Spider
	 * @param out
	 * @param de
	 * @param num_threads
	 * @param isThreadPool
	 * @param maxNum
	 */
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
	
	/**
	 * begin the craw task
	 * @param seed : initial url list
	 */
	public void runTask(List<URL> seed, String task_name, String dto) {
		this.preCraw(seed, (int)(factor * maxNum));
		if(isThreadPool) {
			//TODO thread pool
			//ExecutorService fixedThreadPool = Executors.newFixedThreadPool(num_threads);
			//fixedThreadPool.execute(this);
		}
		else {
			// non thread pool mode
			//step 1: finish crawl taksks
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
			
			//step 2: persist crawled data and visited urls to DB (TODO buffered persistent)
			DBWriter.writeData2DB(total_data, task_name, dto);
			um.flushVisitedURL2DB();
			
			//step 3: output to a file (optional) 
			if(out != null) {
				// output html file
				String path = "";
				if(Util.getCurrentOS() == OperatingSystem.LINUX)
					path = "/home/knshen/test.html";
				else if(Util.getCurrentOS() == OperatingSystem.WINDOWS) 
					path = "f://test.html";
				
				out.output(path, new Date(), task_name, dto);
			}
				
		}
	}
	
	public void run() {
		craw();
	}
	
	/**
	 * logic of crawl process (multi-threaded)
	 */
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
			List<Object> data = de.extract(hp.getDocument()); 
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
	
	/**
	 * pre-crawl is a single thread crawl process used to make 
	 * "to visit" url queue not empty 
	 * @param seed
	 * @param initNum
	 */
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
			List<Object> data = de.extract(hp.getDocument());
			if(data != null && data.size() > 0) 
				total_data.addAll(data);
			
			um.addURLList(new_links);
		}
	}
	
	/**
	 * this is the entrance of demo test
	 * @param args
	 */
	public static void main(String[] args) {
		URL seed = new URL("https://leetcode.com/problemset/algorithms/");
		//create scheduler instance
		DefaultScheduler ds = DefaultScheduler.createDefaultScheduler();
		// config parameters
		ds.config(new HtmlTableOutputer(), new LeetcodeProblemTitleExtractor(), 5, false, 100);
		// run tasks
		ds.runTask(Arrays.asList(seed), "leetcodeProblemTitles", "dto.user.LeetCodeTitleDTO");
	}			

}

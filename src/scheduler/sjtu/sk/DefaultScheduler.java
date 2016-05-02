package scheduler.sjtu.sk;

import java.util.*;

import downloader.sjtu.sk.HtmlDownloader;
import logging.sjtu.sk.Logging;
import parser.sjtu.sk.HtmlParser;
import url.manager.sjtu.sk.URL;
import url.manager.sjtu.sk.URLManager;

public class DefaultScheduler implements Runnable {
	private int num_threads;
	private HtmlDownloader hd = null;
	private HtmlParser hp = null;
	private URLManager um = null;
	private boolean isThreadPool = false;
	
	
	public static DefaultScheduler createDefaultScheduler(int num_threads, boolean isThreadPool) {
		return new DefaultScheduler(num_threads, isThreadPool);
	}
	
	private DefaultScheduler(int num_threads, boolean isThreadPool) {
		this.num_threads = num_threads;
		this.isThreadPool = isThreadPool;
		um = new URLManager();
		hd = new HtmlDownloader();
		hp = new HtmlParser();
	}
	
	public void run() {
		
	}
	
	public void craw(List<URL> seed, int maxNum) {
		um.addURLList(seed);
		int count = 0;
		
		while(true) {
			if(count >= maxNum)
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

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
import parser.sjtu.sk.ImageExtractor;
import parser.sjtu.sk.LeetcodeProblemTitleExtractor;
import storage.sjtu.sk.DataWriter;
import url.manager.sjtu.sk.URL;
import url.manager.sjtu.sk.URLManager;
import util.sjtu.sk.OperatingSystem;
import util.sjtu.sk.PersistentStyle;
import util.sjtu.sk.Util;

/**
 * Default scheduler of the Spider
 * Scheduler is the core logic to combine all the modules together
 * This scheduler supports multi-thread working
 * @author ShenKai
 *
 */
public class DefaultScheduler implements Runnable {
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
	private int persistent_style = PersistentStyle.ES; // save data to MongoDB or ElasticSearch?
	
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
	public final void config(Map<String, Object> paras) {
		// config parameters
		if(paras == null)
			return;
		for(Map.Entry<String, Object> para : paras.entrySet()) {
			String key = para.getKey().toLowerCase();
			if(key.equals("outputer"))
				this.out = (Outputer)(para.getValue());
			if(key.equals("dataextractor"))
				this.de = (DataExtractor)(para.getValue());
			if(key.equals("num_threads"))
				this.num_threads = (int)(para.getValue());
			if(key.equals("isthreadpool"))
				this.isThreadPool = (boolean)(para.getValue());
			if(key.equals("maxnum"))
				this.maxNum = (int)(para.getValue());
			if(key.equals("persistent_style"))
				this.persistent_style = (int)(para.getValue());
		}
	}
	
	private DefaultScheduler() {
		um = new URLManager();
		hd = new HtmlDownloader();
		hp = new HtmlParser();
	}
	
	/**
	 * 
	 * @param seed : seed URLs
	 * @param task_name : indexName-typeName
	 * @param dto : User defined data type
	 */
	public void runTask(List<URL> seed, String task_name, String dto) {
		if(isThreadPool) {
			//TODO thread pool
			//ExecutorService fixedThreadPool = Executors.newFixedThreadPool(num_threads);
			//fixedThreadPool.execute(this);
		}
		else {
			// non thread pool mode
			//step 1: finish crawl tasks
			this.um.addURLList(seed);
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
			if(this.persistent_style == PersistentStyle.DB)
				DataWriter.writeData2DB(total_data, task_name, dto);
			else
				DataWriter.writeData2ES(total_data, task_name, dto); 
			//um.flushVisitedURL2DB();
			
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
		crawl();
	}
	
	/**
	 * logic of crawl process (multi-threaded)	public static void main(String[] args) throws IOException {
		DataExtractor de = new ImageExtractor();
		Document doc = Jsoup.connect("http://sports.qq.com/nba/").get();
		de.extract(doc);
	 */
	private void crawl() {
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
					Thread.sleep(1000);
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
	 * this is the entrance of demo test
	 * @param args
	 */
	public static void main(String[] args) {
		
		//demo 1: crawl leetcode problem title
		URL seed = new URL("https://leetcode.com/problemset/algorithms/");
		//create scheduler instance
		DefaultScheduler ds = DefaultScheduler.createDefaultScheduler();
		// config parameters
		Map<String, Object> paras = new HashMap<String, Object>();
		paras.put("OutPuter", new HtmlTableOutputer());
		paras.put("dataExtractor", new LeetcodeProblemTitleExtractor());
		paras.put("num_threads", 10);
		paras.put("isThreadPool", false);
		paras.put("maxNum", 30);
		paras.put("persistent_style", PersistentStyle.ES);
		ds.config(paras);
		
		// run tasks
		ds.runTask(Arrays.asList(seed), "leetcode-problemTitle", "dto.user.LeetCodeTitleDTO");
		
		/*
		//demo 2: crawl images
		URL seed = new URL("http://ent.qq.com/star/");
		DefaultScheduler ds = DefaultScheduler.createDefaultScheduler();
		ds.config(null, new ImageExtractor(), 3, false, 10);
		ds.runTask(Arrays.asList(seed), "qqStarPic", "dto.user.Picture");
		*/
	}			

}

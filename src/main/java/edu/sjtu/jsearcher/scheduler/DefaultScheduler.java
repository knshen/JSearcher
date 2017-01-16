package edu.sjtu.jsearcher.scheduler;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.apache.log4j.Logger;

import edu.sjtu.jsearcher.balance.ConsistentHash;
import edu.sjtu.jsearcher.balance.HashFunction;
import edu.sjtu.jsearcher.balance.Node;
import edu.sjtu.jsearcher.communication.Sender;
import edu.sjtu.jsearcher.communication.URLReceiver;
import edu.sjtu.jsearcher.downloader.HtmlDownloader;
import edu.sjtu.jsearcher.filter.LinkFilter;
import edu.sjtu.jsearcher.outputer.Outputer;
import edu.sjtu.jsearcher.parser.DataExtractor;
import edu.sjtu.jsearcher.parser.HtmlParser;
import edu.sjtu.jsearcher.storage.DataWriter;
import edu.sjtu.jsearcher.storage.MemoryDataWriter;
import edu.sjtu.jsearcher.url.manager.URL;
import edu.sjtu.jsearcher.url.manager.URLComparator;
import edu.sjtu.jsearcher.url.manager.URLManager;
import edu.sjtu.jsearcher.util.BloomFilter;
import edu.sjtu.jsearcher.util.OperatingSystem;
import edu.sjtu.jsearcher.util.PersistentStyle;
import edu.sjtu.jsearcher.util.Util;
import edu.sjtu.jsearcher.util.XMLHandler;
import edu.sjtu.jsearcher.util.YamlHandler;

/**
 * Default scheduler of the Spider
 * Scheduler is the core logic to combine all the modules together
 * This scheduler supports multi-thread working
 * @author ShenKai
 *
 */
public class DefaultScheduler {
	private static Logger logger = Logger.getLogger(DefaultScheduler.class); 
	HtmlDownloader hd = null;
	private HtmlParser hp = null;
	URLManager um = null;
	Outputer out = null;
	DataExtractor de = null;
	private final Lock lock = new ReentrantLock(); 
	private List<Object> total_data = new ArrayList<Object>(); // global crawled data 
	private BloomFilter<URL> already_sent = null;
	
	private int tolerance = 0;
	private int count = 0; // number of pages have visited
	
	//parameters to set(within a scheduler)
	int num_threads; // number of threads
	int maxNum = 0; // max number of pages to visist
	int persistent_style = PersistentStyle.ES; // save data to where?
	String task_name = ""; // task name
	String dto = ""; // path of dto definition

	
	class CrawlTask implements Callable<String> {
		public String call() {
			crawl();
			return null;
		}
		
		/**
		 * logic of crawl process
		 */
		private void crawl() {
			while(true) {
				URL new_url = null;
				// fetch a URL from toVisit list
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
						if(tolerance >= SpiderConfig.MAX_TOLERANCE)
							break;
						Thread.sleep(1000); // wait 1s
						lock.lock();
						try {
							tolerance = Util.increaseOne(tolerance, SpiderConfig.MAX_TOLERANCE);
						} finally {
							lock.unlock();
						}
					} catch(InterruptedException ie) {
						ie.printStackTrace();
					}
					
					continue;
				}
				
				if(count >= maxNum)
					break;
				
				String html = hd.download(new_url);			
				if(html == null) 
					continue;
				
				lock.lock();
				try {
					int _count = Util.increaseOne(count, maxNum);
					if(_count - count != 1)
						break;
					else
						count = _count;
				}
				finally {
					lock.unlock();
				}
				
				logger.info(Thread.currentThread().getName() + " visiting: " + new_url.getURLValue());
									
				List<URL> new_links = hp.parse(html, new_url.getURLValue()); // get new URLs 
				List<Object> data = de.extract(hp.getDocument(html, new_url.getURLValue()), new_url.getURLValue());  // extract data from current page
				
				lock.lock();
				try {
					// add data
					if(data != null && data.size() > 0) 
						total_data.addAll(data); 
					
					// deal with new URLs
					String local_ip = Util.getLocalIP();	
					for(URL url : new_links) {
						Node loc = SpiderConfig.ch.get(url.getURLValue()); // at where should the url be visited
						//if(loc.getNode_id().equals("node-1")) {
						if(loc.getIp().equals(local_ip) || SpiderConfig.cluster.size() <= 1) {
							// local mode or the destination is local node
							um.addOneURL(url); // add locally
						}
						else {
							if(!already_sent.contains(url)) {
								// send this URL to a remote node
								already_sent.addElement(url);
								Sender sender = new Sender("URLQueue", loc.getIp());
								sender.sendMsg(Arrays.asList(url.getURLValue()));
								sender.close();
							}	
						}
					} 
				} 
				finally {
					lock.unlock();
				}
				
				try {
					Thread.sleep(SpiderConfig.POLITENESS);
				} catch(InterruptedException ie) {
					ie.printStackTrace();
				}		
			} // end while
		}
		
	}
	
	public static DefaultScheduler createDefaultScheduler(String configFilePath) {
		return new DefaultScheduler(configFilePath);
	}
	
	private DefaultScheduler(String configFilePath) {
		// initialization
		um = new URLManager();
		hd = new HtmlDownloader();
		hp = new HtmlParser();
		
		already_sent = new BloomFilter<URL>(2<<24);
		
		// configure single scheduler parameters(including storage related parameters):
		SpiderConfig.config(this, configFilePath);
	
		// init cluster info
		SpiderConfig.cluster = YamlHandler.getClusterConfig(configFilePath);
		SpiderConfig.ch = new ConsistentHash<Node>(new HashFunction(), SpiderConfig.NUM_VIRTUAL_NODES, SpiderConfig.cluster);
	}
	
	
	
	/**
	 * 
	 * @param seed : seed URLs
	 * @param task_name : indexName-typeName
	 * @param dto : User defined data type
	 */
	public void runTask(List<URL> seed) {
		ExecutorService pool = Executors.newFixedThreadPool(num_threads);
		
		this.um.addURLList(seed);
		// workers: crawler threads
		// receiver: url receiver thread
		// writer: write in-memory data to disk conditionally
		if(SpiderConfig.cluster.size() > 1) {
			Thread receiver = new Thread(new URLReceiver("URLQueue", um));
			receiver.start();
		}
		
		Thread writer = new MemoryDataWriter(lock, total_data, dto, task_name, persistent_style);
		writer.setPriority(Thread.MAX_PRIORITY); // force to flush data
		writer.start();
		
		List<CrawlTask> workers = new ArrayList<CrawlTask>();
		for(int i=0; i<num_threads; i++) {
			workers.add(new CrawlTask());
		}
		
		try {
			pool.invokeAll(workers);
		} catch(InterruptedException ie) {
			ie.printStackTrace();
		}
					
		pool.shutdown(); //shutdown thread pool
		try {
			writer.interrupt();
			writer.join();
		} catch(InterruptedException ie) {
			ie.printStackTrace();
		}
		
		logger.info("count: " + this.count + "\n");
		
		// in the end, flush remaining data to DB/ES/others
		logger.info("After finishing task: before writing, size: " + total_data.size());
		if (this.persistent_style == PersistentStyle.MONGO && total_data.size() > 0)
			DataWriter.writeData2MongoDB(total_data, task_name, dto);
		else if(this.persistent_style == PersistentStyle.MYSQL && total_data.size() > 0) 
			DataWriter.writeData2MySQL(total_data, task_name, dto);
		else if(this.persistent_style == PersistentStyle.OTHER && total_data.size() > 0) {
			// Optionally output to a file(like json or cvs) 
			// Note: if the PersistentStyle is OTHER, all data will be saved at the end of task
			if(out != null) 	
				out.output(task_name, total_data);
		}
		else if(this.persistent_style == PersistentStyle.ES && total_data.size() > 0)
			//By default, data must be flushed into ES
			DataWriter.writeData2ES(total_data, task_name, dto);
		logger.info("finished the task!");
	}
	
	
	public static void main(String args[]) throws Exception {
		System.out.println(InetAddress.getLocalHost().getHostAddress());
	}

}

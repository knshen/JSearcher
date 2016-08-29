package sjtu.sk.scheduler;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.nodes.Document;

import sjtu.sk.communication.Sender;
import sjtu.sk.communication.URLReceiver;
import sjtu.sk.downloader.HtmlDownloader;
import sjtu.sk.logging.Logging;
import sjtu.sk.outputer.HtmlTableOutputer;
import sjtu.sk.outputer.Outputer;
import sjtu.sk.parser.DataExtractor;
import sjtu.sk.parser.HtmlParser;
import sjtu.sk.parser.ImageExtractor;
import sjtu.sk.parser.LeetcodeProblemExtractor;
import sjtu.sk.parser.LeetcodeProblemTitleExtractor;
import sjtu.sk.storage.DataWriter;
import sjtu.sk.url.manager.URL;
import sjtu.sk.url.manager.URLManager;
import sjtu.sk.util.OperatingSystem;
import sjtu.sk.util.PersistentStyle;
import sjtu.sk.util.Util;
import sjtu.sk.util.XMLReader;
import sjtu.sk.balance.ConsistentHash;
import sjtu.sk.balance.HashFunction;
import sjtu.sk.balance.Node;

/**
 * Default scheduler of the Spider
 * Scheduler is the core logic to combine all the modules together
 * This scheduler supports multi-thread working
 * @author ShenKai
 *
 */
public class DefaultScheduler implements Runnable {
	public static final int numVirtualNodes = 3; // Virtual Node in ConsistentHash
	
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
	
	private String task_name = "";
	private String dto = "";

	private ConsistentHash<Node> ch = null; // load balancer
	private List<Node> cluster = null; // cluster (physical nodes)
	
	public static DefaultScheduler createDefaultScheduler() {
		return new DefaultScheduler();
	}
	
	/**
	 * another way (by XML) to configure parameters of the Spider 
	 * @param configFilePath
	 */
	public final void config(String configFilePath) {
		Map<String, Object> paras = XMLReader.readSchedulerConfig(configFilePath);
		config(paras);
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
		// config other parameters
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
			if(key.equals("task_name"))
				this.task_name = para.getValue().toString();
			if(key.equals("dto"))
				this.dto = para.getValue().toString();
		}
	}
	
	private DefaultScheduler() {
		// initialization
		um = new URLManager();
		hd = new HtmlDownloader();
		hp = new HtmlParser();
		cluster = new ArrayList<Node>();
		
		// init cluster info
		cluster = XMLReader.readClusterConfig("cluster.xml");
		ch = new ConsistentHash<Node>(new HashFunction(), numVirtualNodes, cluster);
	}
	
	/**
	 * 
	 * @param seed : seed URLs
	 * @param task_name : indexName-typeName
	 * @param dto : User defined data type
	 */
	public void runTask(List<URL> seed) {
		if(isThreadPool) {
			//TODO thread pool
			//ExecutorService fixedThreadPool = Executors.newFixedThreadPool(num_threads);
			//fixedThreadPool.execute(this);
		}
		else {
			// non thread pool mode
			//step 1: finish crawl tasks
			this.um.addURLList(seed);
			
			// worders: crawler threads
			// receiver: url receiver thread
			Thread receiver = new Thread(new URLReceiver("URLQueue", um));
			receiver.start();
			
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
		//Sender sender = new 
		crawl();
	}
	
	/**
	 * logic of crawl process
	 */
	private void crawl() {
		while(true) {
			if(count >= maxNum)
				break;
			
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
					Thread.sleep(1000); // wait 1s
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
		
			List<URL> new_links = hp.parse(html, new_url.getURLValue()); // get new URLs 
			List<Object> data = de.extract(hp.getDocument());  // extract data from current page
			lock.lock();
			try {
				// add data
				if(data != null && data.size() > 0) 
					total_data.addAll(data); 
				
				// deal with new URLs
				String local_ip = Util.getLocalIP();
				for(URL url : new_links) {
					Node loc = ch.get(url.getURLValue()); // at where should the url be visited
					//if(loc.getNode_id().equals("node-1")) {
					if(loc.getIp().equals(local_ip)) {
						um.addOneURL(url); // add locally
					}
					else {
						Sender sender = new Sender("URLQueue", loc.getIp());
						sender.sendMsg(Arrays.asList(url.getURLValue()));
						sender.close();
					}
				} 
				
				//um.addURLList(new_links);
			} 
			finally {
				lock.unlock();
			}
		} // end while
		
	}
	
	public static void main(String args[]) throws Exception {
		System.out.println(InetAddress.getLocalHost().getHostAddress());
	}

}

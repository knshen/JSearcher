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
import sjtu.sk.filter.LinkFilter;
import sjtu.sk.logging.Logging;
import sjtu.sk.outputer.Outputer;
import sjtu.sk.parser.DataExtractor;
import sjtu.sk.parser.HtmlParser;
import sjtu.sk.parser.ImageExtractor;
import sjtu.sk.storage.DataWriter;
import sjtu.sk.storage.MemoryDataWriter;
import sjtu.sk.url.manager.URL;
import sjtu.sk.url.manager.URLComparator;
import sjtu.sk.url.manager.URLManager;
import sjtu.sk.util.BloomFilter;
import sjtu.sk.util.OperatingSystem;
import sjtu.sk.util.PersistentStyle;
import sjtu.sk.util.Util;
import sjtu.sk.util.XMLHandler;
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
	public static final int NUM_VIRTUAL_NODES = 3; // Virtual Node in ConsistentHash
	public static final int POLITENESS = 500; // a thread sleep for a while after a request
	public static final int MAX_TOLERANCE = 10; // when finding url queue is empty up to 
													   // tolerance_threashold, break!
	
	private HtmlDownloader hd = null;
	private HtmlParser hp = null;
	private URLManager um = null;
	private Outputer out = null;
	private DataExtractor de = null;
	
	private int tolerance = 0; // number of 
	private List<Object> total_data = new ArrayList<Object>(); // global crawled data 
	private int num_threads; // number of threads
	private boolean isThreadPool = false;
	private int count = 0; // number of pages have visited
	private final Lock lock = new ReentrantLock(); 
	private int maxNum = 0; // max number of pages to visist
	private int persistent_style = PersistentStyle.ES; // save data to MongoDB or ElasticSearch?
	private String task_name = "";
	private String dto = "";

	private ConsistentHash<Node> ch = null; // load balancer
	private List<Node> cluster = null; // cluster (physical nodes)
	
	private BloomFilter<URL> already_sent = null;
	
	public static DefaultScheduler createDefaultScheduler() {
		return new DefaultScheduler();
	}
	
	/**
	 * another way (by XML) to configure parameters of the Spider 
	 * @param configFilePath
	 */
	public final void config(String configFilePath) {
		Map<String, Object> paras = XMLHandler.readSchedulerConfig(configFilePath);
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
			if(key.equals("filter")) 
				um.setFilter((LinkFilter)(para.getValue()));
			if(key.equals("comparator")) 
				um.setURLComparator((URLComparator<URL>)(para.getValue()));
		
		}
	}
	
	private DefaultScheduler() {
		// initialization
		um = new URLManager();
		hd = new HtmlDownloader();
		hp = new HtmlParser();
		cluster = new ArrayList<Node>();
		already_sent = new BloomFilter<URL>(2<<24);
		
		// init cluster info
		cluster = XMLHandler.readClusterConfig("cluster.xml");
		ch = new ConsistentHash<Node>(new HashFunction(), NUM_VIRTUAL_NODES, cluster);
	}
	
	public void setProxyTrue() {
		this.hd.setProxyTrue();
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
			// finish crawl tasks
			this.um.addURLList(seed);
			
			// worders: crawler threads
			// receiver: url receiver thread
			// writer: write in-memory data to disk conditionally
			if(cluster.size() > 1) {
				Thread receiver = new Thread(new URLReceiver("URLQueue", um));
				receiver.start();
			}
			
			Thread writer = new Thread(new MemoryDataWriter(lock, total_data, dto, task_name, persistent_style));
			writer.setPriority(Thread.MAX_PRIORITY); // force to flush data
			writer.start();
			
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
			
			//Logging.log("count: " + this.count + "\n");
			
			// flush remaining data to DB/ES/others
			Logging.log("before writing, size: " + total_data.size());
			if (this.persistent_style == PersistentStyle.MONGO)
				DataWriter.writeData2MongoDB(total_data, task_name, dto);
			else if(this.persistent_style == PersistentStyle.MYSQL) 
				DataWriter.writeData2MySQL(total_data, task_name, dto);
			else if(this.persistent_style == PersistentStyle.OTHER)
				// Optionally output to a file(like json or cvs) 
				// Note: if the PersistentStyle is OTHER, all data will be saved at the end of task
				if(out != null) 	
					out.output(task_name, total_data);
				
			else if(this.persistent_style == PersistentStyle.ES)
				//By default, data must be flushed into ES
				DataWriter.writeData2ES(total_data, task_name, dto);
			
		}
		
		Logging.log("finished the task!\n");
	}
	
	public void run() {
		crawl();
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
					if(tolerance >= MAX_TOLERANCE)
						break;
					Thread.sleep(1000); // wait 1s
					lock.lock();
					try {
						tolerance = Util.increaseOne(tolerance, MAX_TOLERANCE);
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
				count = Util.increaseOne(count, maxNum);
			}
			finally {
				lock.unlock();
			}
			
			Logging.log(Thread.currentThread().getName() + " visiting: " + new_url.getURLValue());
		
			List<URL> new_links = hp.parse(html, new_url.getURLValue()); // get new URLs 
			List<Object> data = de.extract(hp.getDocument(), new_url.getURLValue());  // extract data from current page
			
			lock.lock();
			try {
				// add data
				if(data != null && data.size() > 0) 
					total_data.addAll(data); 
				
				if(count >= maxNum)
					break;
				
				if(cluster.size() <= 1) {
					 
				}
				// deal with new URLs
				String local_ip = Util.getLocalIP();	
				for(URL url : new_links) {
					Node loc = ch.get(url.getURLValue()); // at where should the url be visited
					//if(loc.getNode_id().equals("node-1")) {
					if(loc.getIp().equals(local_ip) || cluster.size() <= 1) {
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
				Thread.sleep(POLITENESS);
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}		
		} // end while
	}
	
	public static void main(String args[]) throws Exception {
		System.out.println(InetAddress.getLocalHost().getHostAddress());
	}

}

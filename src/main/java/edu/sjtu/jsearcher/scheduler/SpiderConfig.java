package edu.sjtu.jsearcher.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.sjtu.jsearcher.balance.ConsistentHash;
import edu.sjtu.jsearcher.balance.HashFunction;
import edu.sjtu.jsearcher.balance.Node;
import edu.sjtu.jsearcher.filter.LinkFilter;
import edu.sjtu.jsearcher.outputer.Outputer;
import edu.sjtu.jsearcher.parser.DataExtractor;
import edu.sjtu.jsearcher.url.manager.URL;
import edu.sjtu.jsearcher.url.manager.URLComparator;
import edu.sjtu.jsearcher.util.YamlHandler;

/**
 * SpiderConfig is used to configure various parameters of the spider
 * @author Kai
 *
 */
public class SpiderConfig {
	static int NUM_VIRTUAL_NODES = 3; // Virtual Node in ConsistentHash
	static int POLITENESS = 500; // a thread sleep for a while after a request
	static int MAX_TOLERANCE = 10; // when finding url queue is empty up to tolerance_threashold, break!												   
	static List<Node> cluster = new ArrayList<Node>(); // cluster (physical nodes)
	static ConsistentHash<Node> ch = null; // load balancer
		
	public static String storage_host;
	public static int storage_port;
	public static String db_name;
	public static String db_user;
	public static String db_password;
	public static String ES_cluster_name;
	
	public static void setNumVirtualNodes(int num) {
		NUM_VIRTUAL_NODES = num;
	}
	
	public static void setPoliteness(int politeness) {
		POLITENESS = politeness;
	}
	
	public static void setMaxTolerance(int tolerance) {
		MAX_TOLERANCE = tolerance;
	}
	
	public static void setNumThreads(DefaultScheduler ds, int threads) {
		ds.num_threads = threads;
	}
	
	public static void setMaxPagesToCrawl(DefaultScheduler ds, int num_pages) {
		ds.maxNum = num_pages;
	}
	
	public static void setPersistentStyle(DefaultScheduler ds, int style) {
		ds.persistent_style = style;
	}
	
	public static void setTaskName(DefaultScheduler ds, String taskName) {
		ds.task_name = taskName;
	}
	
	public static void setDTO(DefaultScheduler ds, String dto) {
		ds.dto = dto;
	}
	
	public static void setDataExtractor(DefaultScheduler ds, DataExtractor de) {
		ds.de = de;
	}
	
	public static void setOutputer(DefaultScheduler ds, Outputer out) {
		ds.out = out;
	}
	
	public static void setLinkFilter(DefaultScheduler ds, LinkFilter filter) {
		ds.um.setFilter(filter);
	}
	
	public static void setComparator(DefaultScheduler ds, URLComparator<URL> com) {
		ds.um.setURLComparator(com);
	}
	
	/**
	 * Configure single spider scheduler through YAML 
	 * @param configFilePath
	 */
	public static final void config(DefaultScheduler ds, String configFilePath) {
		Map<String, Object> paras = YamlHandler.readSingleSchedulerConfig(configFilePath);
		for(Map.Entry<String, Object> para : paras.entrySet()) {
			String key = para.getKey().toLowerCase();
			if(key.equals("num_threads"))
				ds.num_threads = Integer.parseInt(para.getValue().toString());
			if(key.equals("maxnum"))
				ds.maxNum = Integer.parseInt(para.getValue().toString());
			if(key.equals("persistent_style"))
				ds.persistent_style = Integer.parseInt(para.getValue().toString());
			if(key.equals("task_name"))
				ds.task_name = para.getValue().toString();
			if(key.equals("dto"))
				ds.dto = para.getValue().toString();
		}
		
		// configure storage related parameters
		paras = YamlHandler.getStorageConfig(configFilePath);
		for(Map.Entry<String, Object> para : paras.entrySet()) {
			String key = para.getKey().toLowerCase();
			if(key.equals("host"))
				storage_host = para.getValue().toString();
			if(key.equals("port"))
				storage_port = Integer.parseInt(para.getValue().toString());
			if(key.equals("db"))
				db_name = para.getValue().toString();
			if(key.equals("user"))
				db_user = para.getValue().toString();
			if(key.equals("password"))
				db_password = para.getValue().toString();
			if(key.equals("cluster_name"))
				ES_cluster_name = para.getValue().toString();
		}
	}
	
	public static void setProxyTrue(DefaultScheduler ds) {
		ds.hd.setProxyTrue();
	}
}

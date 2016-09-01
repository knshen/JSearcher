package sjtu.sk.url.manager;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sjtu.sk.filter.LeetcodeURLFilter;
import sjtu.sk.filter.URLFilter;
import sjtu.sk.storage.DataWriter;
import sjtu.sk.util.BloomFilter;

/**
 * Manage "visited" urls and "to visit" urls
 * Note that the operations to the data structures are not thread secure
 * @author ShenKai
 *
 */
public class URLManager {
	// "to visit" urls queue (in memory)
	volatile private Queue<URL> urls = new LinkedList<URL>();
	//volatile private Set<URL> visited = new HashSet<URL>();
	// bloom filter to check if a url is visited
	volatile private BloomFilter<URL> bf_visited = new BloomFilter<URL>(2<<24);
	// must ensure the URL in the list is distinct
	//volatile private List<URL> visited = new ArrayList<URL>();
	private URLFilter filter = null;
	private URLComparator<URL> com = null;
	
	public void setFilter(URLFilter filter) {
		this.filter = filter;
	}
	
	public void setURLComparator(URLComparator<URL> com) {
		this.com = com;
		urls = new PriorityQueue<URL>(com);
	}
	
	public boolean isVisited(URL url) {
		return bf_visited.contains(url);
	}
	
	public int size() {
		return urls.size();
	}
	
	/*
	public void flushVisitedURL2DB() {
		DBWriter.writeVisitedURL2DB(visited);
		visited = new ArrayList<URL>();
	}*/
	
	public boolean addOneURL(URL new_url) {
		if(!isVisited(new_url)) {
			if(filter == null || filter.shouldVisit(new_url)) {
				urls.offer(new_url);
				bf_visited.addElement(new_url);
				return true;
			}
		}
		return false;
	}
	
	public boolean addURLList(List<URL> list) {
		for(URL new_url : list) {
			addOneURL(new_url);	
		}
		return true;
	}
	
	public URL fetchOneURL() {
		if(!urls.isEmpty()) {
			//visited.add(urls.peek());
			
			//if(visited.size() >= MAX_SIZE_VISITED) 
			//	this.flushVisitedURL2DB();
			
			return urls.poll();
		}
		return null;
	}
	
	public static void main(String args[]) {
		// unit test
		URLManager um = new URLManager();
		um.setURLComparator(new LeetcodeURLComparator());
		um.addOneURL(new URL("https://leetcode.com/problemset/algorithms/"));
		um.addOneURL(new URL("https://leetcode.com/contest/"));
		um.addOneURL(new URL("https://leetcode.com/problems/find-the-difference/"));
		um.addOneURL(new URL("https://leetcode.com/problems/super-pow/"));
		
		for(int i=0; i<4; i++)
			System.out.println(um.fetchOneURL().print());
	}
	
}

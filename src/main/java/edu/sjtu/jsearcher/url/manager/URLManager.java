package edu.sjtu.jsearcher.url.manager;

import java.util.*;

import edu.sjtu.jsearcher.filter.LinkFilter;
import edu.sjtu.jsearcher.util.BloomFilter;

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
	private LinkFilter filter = null;
	
	public void setFilter(LinkFilter filter) {
		this.filter = filter;
	}
	
	public void setURLComparator(URLComparator<URL> com) {
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
			Link link = null;
			if(new_url instanceof Link)
				link = (Link)new_url;
			if(link == null || filter == null || (link != null && filter.shouldVisit(link))) {
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
	}
	
}

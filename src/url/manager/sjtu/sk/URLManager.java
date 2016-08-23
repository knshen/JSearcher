package url.manager.sjtu.sk;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import storage.sjtu.sk.DataWriter;
import util.sjtu.sk.BloomFilter;

/**
 * Manage "visited" urls and "to visit" urls
 * Note that the operations to the data structures are not thread secure
 * @author ShenKai
 *
 */
public class URLManager {
	public static final int MAX_SIZE_VISITED = 10000;
	// "to visit" urls queue (in memory)
	volatile private Queue<URL> urls = new LinkedList<URL>();
	//volatile private Set<URL> visited = new HashSet<URL>();
	// bloom filter to check if a url is visited
	volatile private BloomFilter<URL> bf_visited = new BloomFilter<URL>(2<<24);
	// must ensure the URL in the list is distinct
	//volatile private List<URL> visited = new ArrayList<URL>();
			
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
			urls.offer(new_url);
			bf_visited.addElement(new_url);
			return true;
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
		for(int i=0; i<3; i++) {
			String url = "abcd";
			um.addOneURL(new URL(url));
		}
		for(int i=0; i<2; i++) {
			String url = "abcx";
			um.addOneURL(new URL(url));
		}
		System.out.println();
		URL a = null;
		a = um.fetchOneURL();
		a = um.fetchOneURL();
	}
	
}

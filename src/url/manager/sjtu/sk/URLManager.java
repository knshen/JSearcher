package url.manager.sjtu.sk;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class URLManager {
	volatile private Queue<URL> urls = new LinkedList<URL>();
	volatile private Set<URL> visited = new HashSet<URL>();
	
	public boolean isVisited(URL url) {
		return visited.contains(url);
	}
	
	public boolean addOneURL(URL new_url) {
		Lock lock = new ReentrantLock();
		lock.lock(); 
		try {
			if(!isVisited(new_url)) {
				urls.offer(new_url);
				visited.add(new_url);
				return true;
			}
			return false;
		}
		finally {
			lock.unlock();
		}
		
	}
	
	public boolean addURLList(List<URL> list) {
		for(URL new_url : list) {
			addOneURL(new_url);	
		}
		return true;
	}
	
	public URL fetchOneURL() {
		Lock lock = new ReentrantLock();
		lock.lock(); 
		try {
			if(!urls.isEmpty()) {
				return urls.poll();
			}
			return null;
		}
		finally {
			lock.unlock();
		}
	}
	
	public static void main(String args[]) {
		// unit test
		URLManager um = new URLManager();
		for(int i=0; i<3; i++) {
			String url = "abcd";
			um.addOneURL(new URL(url));
		}
		System.out.println();
		URL a = null;
		a = um.fetchOneURL();
		a = um.fetchOneURL();
	}
	
}

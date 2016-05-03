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
	
	public int size() {
		return urls.size();
	}
	
	public boolean addOneURL(URL new_url) {
		if(!isVisited(new_url)) {
			urls.offer(new_url);
			visited.add(new_url);
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
		System.out.println();
		URL a = null;
		a = um.fetchOneURL();
		a = um.fetchOneURL();
	}
	
}

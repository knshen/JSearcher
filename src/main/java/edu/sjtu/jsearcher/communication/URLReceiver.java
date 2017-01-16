package edu.sjtu.jsearcher.communication;

import edu.sjtu.jsearcher.url.manager.URL;
import edu.sjtu.jsearcher.url.manager.URLManager;

public class URLReceiver extends Receiver {
	private URLManager um = null;
	
	public URLReceiver(String queue_name) {
		super(queue_name);
	}
	
	public URLReceiver(String queue_name, URLManager um) {
		super(queue_name);
		this.um = um;
	}
	
	public void afterRecvMsg(String msg) {
		// TODO put msg(url) into local url queue(in memory)
		URL url = new URL(msg);
		um.addOneURL(url);
	}
	
	public static void main(String[] args) {
    	Thread listener = new Thread(new URLReceiver("FirstQueue"));
    	listener.start();
    }
}

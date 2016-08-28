package sjtu.sk.communication;

public class URLReceiver extends Receiver {
	
	public URLReceiver(String queue_name) {
		super(queue_name);
	}
	
	public void afterRecvMsg(String msg) {
		// TODO check whether msg(url) is duplicated
		// TODO put msg(url) into local url queue(in memory)
	}
	
	public static void main(String[] args) {
    	Thread listener = new Thread(new URLReceiver("FirstQueue"));
    	listener.start();
    }
}

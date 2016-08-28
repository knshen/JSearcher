package sjtu.sk.balance;

public class Node {
	private String ip;
	private String node_id;
	
	public Node(String node_id, String ip) {
		this.node_id = node_id;
		this.ip = ip;
	}
	
	public String toString() {
		return node_id + "(" + ip + ")";
	}
}

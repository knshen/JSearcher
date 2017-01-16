package edu.sjtu.jsearcher.balance;

public class Node {
	private String ip;
	private String node_id;
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getNode_id() {
		return node_id;
	}

	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}

	public Node(String node_id, String ip) {
		this.node_id = node_id;
		this.ip = ip;
	}
	
	public String toString() {
		return node_id + "(" + ip + ")";
	}
}

package edu.sjtu.jsearcher.balance;

import java.util.*;


public class ConsistentHash<T> {
	private final HashFunction hashFunction;
	private final int numberOfReplicas;// number of virtual nodes
	// mapping: virtual node -> physical node
	private final SortedMap<Long, T> circle = new TreeMap<Long, T>();

	/**
	 * 
	 * @param hashFunction
	 * @param numberOfReplicas
	 * @param nodes : physical nodes
	 */
	public ConsistentHash(HashFunction hashFunction, int numberOfReplicas,
			Collection<T> nodes) {
		this.hashFunction = hashFunction;
		this.numberOfReplicas = numberOfReplicas;
		for (T node : nodes) {
			add(node);
		}
	}

	/**
	 * add a physical node
	 * @param node
	 */
	public void add(T node) {
		for (int i = 0; i < numberOfReplicas; i++) {
			circle.put(hashFunction.hash(node.toString() + i), node);
		}
	}

	/**
	 * remove a physical node
	 * @param node
	 */
	public void remove(T node) {
		for (int i = 0; i < numberOfReplicas; i++) {
			circle.remove(hashFunction.hash(node.toString() + i));
		}
	}

	/**
	 * given the input, get target physical node
	 * @param key
	 * @return
	 */
	public T get(Object key) {
		if (circle.isEmpty()) {
			return null;
		}
		// 计算hash值
		long hash = hashFunction.hash(key);
		// 如果不包括这个hash值
		if (!circle.containsKey(hash)) {
			SortedMap<Long, T> tailMap = circle.tailMap(hash);
			hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
		}

		return circle.get(hash);
	}
	
	public static void main(String args[]) {
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(new Node("A", "192.168.0.1"));
		//nodes.add(new Node("B", "192.168.0.2"));
		//nodes.add(new Node("C", "192.168.0.3"));
		
		ConsistentHash<Node> ch = new ConsistentHash<Node>(new HashFunction(), 3, nodes);
		
		List<String> keys = new ArrayList<String>();
		keys.add("https://leetcode.com/problems/perfect-rectangle/");
		keys.add("https://leetcode.com/problems/ransom-note/");
		keys.add("https://leetcode.com/problems/super-ugly-number/");
		keys.add("https://leetcode.com/contest/");
		keys.add("https://leetcode.com/");
	
		
		for(String key : keys) {
			System.out.println(ch.get(key));
		}
	}
}

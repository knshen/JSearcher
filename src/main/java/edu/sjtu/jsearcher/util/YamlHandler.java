package edu.sjtu.jsearcher.util;

import org.ho.yaml.Yaml;

import edu.sjtu.jsearcher.balance.Node;

import java.io.*;
import java.util.*;


public class YamlHandler {
	public static Map<String, Object> readSingleSchedulerConfig(String filePath) {
		Map<String, Object> res = new HashMap<String, Object>();
		File file = new File(filePath);
		try {
			Map<String, Object> map = Yaml.loadType(file, HashMap.class);
			for(Map.Entry<String, Object> entry : map.entrySet()) {
				if(entry.getValue() instanceof Map)
					continue;
				res.put(entry.getKey(), entry.getValue());
			}
			
		} catch(FileNotFoundException fe) {
			fe.printStackTrace();
		}
        
		return res;
	}
	
	public static Map<String, Object> getStorageConfig(String filePath) {
		Map<String, Object> res = new HashMap<String, Object>();
		File file = new File(filePath);
		try {
			Map<String, Object> map = Yaml.loadType(file, HashMap.class);
			for(Map.Entry<String, Object> entry : map.entrySet()) {
				if(entry.getValue() instanceof Map && !entry.getKey().equals("cluster")) {
					Map<String, Object> paras = (HashMap<String, Object>)entry.getValue();
					res.put("host", paras.get("host"));
					res.put("port", paras.get("port"));
					
					if(entry.getKey().equals("mysql")) {
						res.put("db", paras.get("db"));
						res.put("user", paras.get("user"));
						res.put("password", paras.get("password"));
					}
					else if(entry.getKey().equals("mongodb")) {
						res.put("db", paras.get("db"));
					}
					else if(entry.getKey().equals("es")) {
						res.put("cluster_name", paras.get("cluster_name"));
					}
				}
			}
			
		} catch(FileNotFoundException fe) {
			fe.printStackTrace();
		}
		return res;
	}
	
	public static List<Node> getClusterConfig(String filePath) {
		List<Node> cluster = new ArrayList<Node>();
		
		File file = new File(filePath);
		try {
			Map<String, Object> map = Yaml.loadType(file, HashMap.class);
			for(Map.Entry<String, Object> entry : map.entrySet()) {
				if(entry.getValue() instanceof Map && entry.getKey().equals("cluster")) {
					map = (HashMap<String, Object>)entry.getValue();
					break;
				}
			}
			
			for(Map.Entry<String, Object> entry : map.entrySet()) {
				Map<String, Object> tuple = (HashMap<String, Object>)entry.getValue();
				Node node = new Node(tuple.get("name").toString(), tuple.get("ip").toString());
				cluster.add(node);
			}
			
		} catch(FileNotFoundException fe) {
			fe.printStackTrace();
		}
		
		return cluster;
	}
	
	public static void main(String[] args) {
		System.out.println(YamlHandler.readSingleSchedulerConfig("JSearcher.yml"));
		System.out.println(YamlHandler.getClusterConfig("JSearcher.yml"));
		System.out.println(YamlHandler.getStorageConfig("JSearcher.yml"));
	}

}

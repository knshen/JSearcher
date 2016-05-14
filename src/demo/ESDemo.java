package demo;

import java.net.InetAddress;
import java.util.*;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;


public class ESDemo {
	static Client client = null;
	
	public static void init() throws Exception {
		// build TransportClient 
		Settings settings = Settings.settingsBuilder().
				put("cluster.name", "knshen-cluster").
				put("client.transport.sniff", true).
				build();
		
		client = TransportClient.builder().settings(settings).build().
				addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
	}
	
	public static void insert() {
		Map<String, Object> data1 = new HashMap<String, Object>();
		Map<String, Object> data2 = new HashMap<String, Object>();
		Map<String, Object> data3 = new HashMap<String, Object>();
		
		data1.put("name", "Kobe");
		data1.put("age", 38);
		data1.put("location","SG");
		data1.put("insterst", "basketball business film");
		
		data2.put("name", "James");
		data2.put("age", 32);
		data2.put("location","SF");
		data2.put("insterst", "basketball singing dance joking");
		
		data3.put("name", "Curry");
		data3.put("age", 28);
		data3.put("location","PG");
		data3.put("insterst", "basketball joking maimeng smile");
		
		client.prepareIndex("nba", "player")
				.setSource(data1).execute().actionGet();
		client.prepareIndex("nba", "player")
				.setSource(data2).execute().actionGet();
		client.prepareIndex("nba", "player")
				.setSource(data3).execute().actionGet();
		/*
		// Index name
		String _index = response.getIndex();
		// Type name
		String _type = response.getType();
		// Document ID (generated or not)
		String _id = response.getId();
		// Version (if it's the first time you index this document, you will get: 1)
		long _version = response.getVersion();
		// isCreated() is true if the document is a new one, false if it has been updated
		boolean created = response.isCreated();
		*/

	}
	
	public static void search() {
		SearchResponse response = client.prepareSearch("nba")
		        .setTypes("player")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		  		//.setQuery(QueryBuilders.matchQuery("insterst", "business maimeng"))
		        //.setQuery(QueryBuilders.termQuery("location", "G"))                 // Query
		        //.setPostFilter(QueryBuilders.rangeQuery("age").from(30).to(40))     // Filter
		        //.setFrom(0).setSize(60).setExplain(true)		     
		        .execute()
		        .actionGet();
		
		SearchHits hits = response.getHits();
		
		System.out.println("hits: " + hits.getTotalHits());
		for(SearchHit sh : hits.getHits()) 
			System.out.println(sh.getSourceAsString() + " " + sh.getScore());
		
	}
	
	public static void close() {
		client.close();
	}
	
	public static void main(String[] args) throws Exception {
		ESDemo.init();
		//ESDemo.insert();
		ESDemo.search();
		ESDemo.close();

	}

}

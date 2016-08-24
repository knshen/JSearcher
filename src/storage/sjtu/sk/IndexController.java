package storage.sjtu.sk;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import logging.sjtu.sk.Logging;
import util.sjtu.sk.Util;

public class IndexController {
	public static double PAGE_SIZE = 1000.0;
	private static IndexController ic = null;
	private static Client client = null;	
	
	private IndexController(InetAddress ia) {
		// build TransportClient 
		Settings settings = Settings.settingsBuilder().
				put("cluster.name", "knshen-cluster").
				put("client.transport.sniff", true).
				build();
		
		client = TransportClient.builder().settings(settings).build().
				addTransportAddress(new InetSocketTransportAddress(ia, 9300));
	}
	
	public static IndexController createIndexControllerInstance(String master_ip) {
		if(ic == null) {
			synchronized(IndexController.class) {
				if(ic == null) {
					try {
						ic = new IndexController(InetAddress.getByName("localhost"));
					} catch(UnknownHostException uhe) {
						uhe.printStackTrace();
					}
					
				}
			}
		}
		return ic;
	}
	
	/**
	 * insert a list of data to ElasticSearch
	 * @param data
	 * @param index
	 * @param type
	 * @param dto
	 * @return
	 */
	public boolean insert(List<Object> data, String index, String type, String dto) {
		try {
			for(Object pojo : data) {
				Map<String, Object> map = Util.serialize(dto, pojo);
				
				client.prepareIndex(index, type)
				.setSource(map).execute().actionGet();
			}
			
			return true;
		} catch(IllegalAccessException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch(InvocationTargetException ite) {
			ite.printStackTrace();
		}
		
		return false;
	}
		
	/**
	 * return all the data of a type
	 * @param index
	 * @param type
	 * @return
	 */
	public List<Object> searchAll(String index, String type, String dto) {
		List<Object> res = new ArrayList<Object>();
		
		SearchResponse response = null;
		SearchRequestBuilder srb = client.prepareSearch(index)
		        	.setTypes(type)	
		        	.setSearchType(SearchType.SCAN)
		        	.setScroll(new TimeValue(60000))
		        	.setQuery(QueryBuilders.matchAllQuery())
		        	.setSize(5);
		
		response = srb.execute().actionGet();
		while(true) {
			SearchHits hits = response.getHits();
			for(SearchHit sh : hits.getHits()) {
				try {
					Map<String, Object> map = sh.getSource();
					res.add(Util.deserialize(map, dto));
					Logging.log(sh.getSourceAsString() + " " + sh.getScore() + "\n");

				} catch(ClassNotFoundException cnfe) {
		        	cnfe.printStackTrace();
		        } catch(InstantiationException ie) {
		        	ie.printStackTrace();
		        } catch(IllegalAccessException iae) {
		        	iae.printStackTrace();
		        } catch(InvocationTargetException ite) {
	    			ite.printStackTrace();
	    		}     		
			} // end for
			System.out.println("-------------------");
			response = client.prepareSearchScroll(response.getScrollId())
						.setScroll(new TimeValue(60000)).execute().actionGet();
		
			if (response.getHits().getHits().length == 0) 
		        break;
			
		} // end while
	
		return res;
	}
	
	public long count(String index, String type) {
		SearchResponse response = client.prepareSearch(index)
		        .setTypes(type)
		        .setQuery(QueryBuilders.matchAllQuery())               
		        .setFrom(0).setSize(0).setExplain(true)		     
		        .execute()
		        .actionGet();
		
		//System.out.println(response.getHits().getTotalHits());
		return response.getHits().getTotalHits();
	}
	
	public void delete(String index, String type) {
		long num = count(index, type);
		SearchResponse response = client.prepareSearch(index)
		        .setTypes(type)
		        .setQuery(QueryBuilders.matchAllQuery())               
		        .setFrom(0).setSize((int)num).setExplain(true)	// TODO :limit page size     
		        .execute()
		        .actionGet();
		SearchHits hits = response.getHits();
		
		List<String> keys = new ArrayList<String>();
		//System.out.println("hits: " + hits.getTotalHits());
		for(SearchHit sh : hits.getHits()) 
			keys.add(sh.getId());
					
		//System.out.println(keys.size());
		for(String key : keys) {
			client.prepareDelete()
					.setIndex(index)
					.setType(type)
					.setId(key)
					.execute()
					.actionGet();
		}
	}

	public static void main(String[] args) {
		IndexController ic = IndexController.createIndexControllerInstance("loalhost");
		//ic.searchAll("leetcode", "problemTitle", "dto.user.LeetCodeTitleDTO");	
		System.out.println(ic.count("leetcode", "problemTitle"));
	}

}

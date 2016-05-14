package index.sjtu.sk;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class IndexController {
	private static IndexController ic = null;
	private static Client client = null;	
	
	private IndexController() {
		// build TransportClient 
		Settings settings = Settings.settingsBuilder().
				put("cluster.name", "knshen-cluster").
				put("client.transport.sniff", true).
				build();
		
		try {
			client = TransportClient.builder().settings(settings).build().
					addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

		} catch(UnknownHostException uhe) {
			uhe.printStackTrace();
		}
	}
	
	public static IndexController createIndexControllerInstance() {
		if(ic == null) {
			synchronized(IndexController.class) {
				if(ic == null) {
					ic = new IndexController();
				}
			}
		}
		return ic;
	}
	
	public boolean insert2Index(String index, String type, List<Map<String, Object>> data) {
		
		return true;
	}
	
	public static void main(String[] args) {
		IndexController ic = IndexController.createIndexControllerInstance();
		
	}

}

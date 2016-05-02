package downloader.sjtu.sk;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import logging.sjtu.sk.Logging;
import url.manager.sjtu.sk.URL;

public class HtmlDownloader {
	public String download(URL url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();  
        
		try {
	        HttpGet httpget = new HttpGet(url.getURLValue());  
	        //Logging.log("executing request " + httpget.getURI());
	        // 执行get请求.    
	        CloseableHttpResponse response = httpclient.execute(httpget);  
	        HttpEntity entity = response.getEntity();  
	        //Logging.log(response.getStatusLine().toString());
	       
	        if(response.getStatusLine().getStatusCode() == 200) {
	        	return EntityUtils.toString(entity).trim();
	        }
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} 
         
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		HtmlDownloader hd = new HtmlDownloader();
		System.out.println(hd.download(new URL("https://leetcode.com/problemset/algorithms/")));
	}

}

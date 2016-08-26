package sjtu.sk.downloader;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import sjtu.sk.logging.Logging;
import sjtu.sk.url.manager.URL;

/**
 * Given a URL, download the html of a page
 * @author ShenKai
 *
 */
public class HtmlDownloader {
	
	/**
	 * download html by a URL (by get method in HttpClient)
	 * @param url : absolute url 
	 * @return html code
	 */
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

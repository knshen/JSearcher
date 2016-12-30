package sjtu.sk.downloader;

import java.io.*;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import sjtu.sk.logging.Logging;
import sjtu.sk.url.manager.URL;
import sjtu.sk.util.Util;

/**
 * Given a URL, download the html of a page
 * 
 * @author ShenKai
 *
 */
public class HtmlDownloader {
	public static int PROXY_RETRY = 10;
	private boolean enableProxy = false;
	private List<String> proxy_pool = null; // each item: proxyIp proxyPort

	public void setProxyTrue() {
		if(this.enableProxy)
			return;
		this.enableProxy = true;
		proxy_pool = new ArrayList<String>();
		try {
			String item = "";
			BufferedReader br = new BufferedReader(new FileReader(new File(
					"proxy.txt")));
			while ((item = br.readLine()) != null) {
				proxy_pool.add(item);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * download html by a URL (by get method in HttpClient)
	 * 
	 * @param url
	 *            : absolute url
	 * @return html code
	 */
	public String download(URL url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url.getURLValue());

		int trys = 0;
		
		while (true) {
			try {
				HttpHost proxy = null;
				RequestConfig config = null;
				if (this.enableProxy) {
					// randomly select a proxy server from proxy pool
					int index = Util.getRandomInteger(0, proxy_pool.size());
					String proxy_host = proxy_pool.get(index).split(" ")[0];
					int proxy_port = Integer.parseInt(proxy_pool.get(index)
							.split(" ")[1]);

					proxy = new HttpHost(proxy_host, proxy_port, "http");
					config = RequestConfig.custom().setProxy(proxy).build();
					httpget.setConfig(config);
				}

				// 执行get请求.
				trys++;
				CloseableHttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();
				// Logging.log(response.getStatusLine().toString());

				if (response.getStatusLine().getStatusCode() == 200) {
					return EntityUtils.toString(entity).trim();
				}

			} catch (Exception e) {
				System.out.println("proxy server not available!");
				if(trys >= PROXY_RETRY)
					return null;
				continue;
			}

		}
	}

	public static void main(String[] args) throws Exception {
		HtmlDownloader hd = new HtmlDownloader();
		System.out.println(hd.download(new URL("http://www.ifeng.com")));
	}

}

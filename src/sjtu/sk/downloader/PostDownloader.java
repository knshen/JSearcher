package sjtu.sk.downloader;

import java.io.*;
import java.util.*;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import sjtu.sk.url.manager.URL;

/**
 * Build a post request by a given URL
 * It is implemented by invoking APIs of HtmlUnit
 * Compared with HttpClient, HtmlUnit is a more high-level lib, where you only need to care about
 * html elements rather than form data.
 * @author Kai
 *
 */
public class PostDownloader {
	private static WebClient webClient = new WebClient(BrowserVersion.CHROME);  
	private static CookieManager cookieMan = null;
	
	public PostDownloader() {
		webClient = new WebClient(BrowserVersion.CHROME);  
		webClient.getOptions().setCssEnabled(false); // disable css loader
		webClient.getOptions().setJavaScriptEnabled(false); // disable js loader
		cookieMan = webClient.getCookieManager();
		cookieMan.setCookiesEnabled(true); // enable cookie
	}
	
	/**
	 * return the html of url
	 * @param url : form data
	 * @param inputs : input name -> input value
	 * @return
	 */
	public String download(URL url, Map<String, String> inputs) {
		HtmlSubmitInput button = null;
		HtmlPage nextPage = null;
		
		try {
			HtmlPage page = webClient.getPage(url.getURLValue()); 
			for(Map.Entry<String, String> input : inputs.entrySet()) {
				String form_name = input.getKey().split("\\.")[0];
				String input_name = input.getKey().split("\\.")[1];
				
				HtmlForm form = page.getFormByName(form_name);
				if(input_name.equals("button")) {
					button = form.getInputByValue(input.getValue());
				}
				else {
					HtmlTextInput text_input = form.getInputByName(input_name);
					text_input.setValueAttribute(input.getValue());
				}
			}
			nextPage = button.click();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		  
		return nextPage.asXml();
	}
		
	public static void main(String[] args) {
		PostDownloader pd = new PostDownloader();
		Map<String, String> inputs = new HashMap<String, String>();
		inputs.put("searchForm.w", "上海 必胜客");
		inputs.put("searchForm.button", "搜索");
		
		String html = pd.download(new URL("http://sh.meituan.com/"), inputs);
		System.out.println(html);
	}

}

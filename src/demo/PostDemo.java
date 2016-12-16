package demo;

import java.util.*;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class PostDemo {
	protected static WebClient webClient = new WebClient(BrowserVersion.CHROME);  
	private static CookieManager cookieMan = null;
	
	public PostDemo() {
		webClient = new WebClient(BrowserVersion.CHROME);  
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		
		cookieMan = webClient.getCookieManager();
		cookieMan.setCookiesEnabled(true);
	}
	
	public void searchInBaidu() throws Exception {
		HtmlPage page = webClient.getPage("https://www.baidu.com/");  
		HtmlForm form = page.getFormByName("f");
		
		HtmlTextInput input = form.getInputByName("wd");	
		HtmlSubmitInput button = form.getInputByValue("百度一下");  
		
		input.setValueAttribute("无锡");
		HtmlPage nextPage = button.click();  
		
		//System.out.println(nextPage.asXml());
		
		// hit next page
		HtmlAnchor next = null;
		List list = nextPage.getByXPath("//a");
		for(Object obj : list) {
			if(obj instanceof HtmlAnchor) {
				HtmlAnchor ha = (HtmlAnchor)obj;
				//System.out.println(ha.getTextContent());
				if(ha.getTextContent().indexOf("百度百科") != -1) {
					next = ha;
					break;
				}
			}
		}
		
		
		System.out.println(next.asXml());
		System.out.println("--------------------------");
		HtmlPage p = next.click();
		System.out.println(p.asXml());
		
	}
	
	public static void main(String[] args) throws Exception {
		PostDemo pd = new PostDemo();
		pd.searchInBaidu();
		//pd.searchCtripTrain();
	}

}

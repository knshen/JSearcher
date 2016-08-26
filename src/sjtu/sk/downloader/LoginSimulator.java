package sjtu.sk.downloader;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;

import sjtu.sk.url.manager.URL;

public abstract class LoginSimulator {
	protected static WebClient webClient = new WebClient(BrowserVersion.CHROME);  
	private static CookieManager cookieMan = null;
	
	public LoginSimulator() {
		webClient = new WebClient(BrowserVersion.CHROME);  
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		cookieMan = webClient.getCookieManager();
		cookieMan.setCookiesEnabled(true);
	}
	
	public abstract String mockLogin(URL url, String username, String password);
}

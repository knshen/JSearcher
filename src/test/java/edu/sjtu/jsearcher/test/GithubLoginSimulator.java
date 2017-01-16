package edu.sjtu.jsearcher.test;

import java.io.*;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import edu.sjtu.jsearcher.downloader.LoginSimulator;
import edu.sjtu.jsearcher.url.manager.URL;

public class GithubLoginSimulator extends LoginSimulator {
	public GithubLoginSimulator() {
		super();
	}
	
	public String mockLogin(URL url, String username, String password) {
		try {
			HtmlPage page1 = webClient.getPage(url.getURLValue());  
			// find the login form
	        HtmlForm form = page1.getForms().get(0);  
	        
	        // fill in the input
	        HtmlInput hi = form.getInputByName("commit");
	        HtmlTextInput textField = form.getInputByName("login");  
	        HtmlPasswordInput pass = form.getInputByName("password");  
	        
	        textField.click();
	        textField.setValueAttribute(username);  
	        pass.click();
	        pass.setValueAttribute(password);  
	      
	        // push the button
	        HtmlPage page2 = hi.click();  
	        return page2.asXml();

		} catch(IOException ioe)  {
			ioe.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		GithubLoginSimulator ghs = new GithubLoginSimulator();
		String html = ghs.mockLogin(new URL("https://github.com/login"), "knshen", "sk530530");
		System.out.println(html);
	}

}

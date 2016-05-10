package demo;

import java.util.*;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class HtmlUnitDemo {
	public static void loginGithub() throws Exception {
		final WebClient webClient = new WebClient(BrowserVersion.getDefault());  
        //1.获取某个待测页面  
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		
		
		//HtmlPage pageTest = webClient.getPage("http://my.csdn.net/my/mycsdn");
		//System.out.println(pageTest.asText());
		final HtmlPage page1 = webClient.getPage("https://github.com/login");  
        //2.获取页面上的表单  
		//System.out.println(page1.asText());
		
        final HtmlForm form = page1.getForms().get(0);  
        //3.获取页面上的各个元素  
        
        HtmlInput hi = form.getInputByName("commit");
        //final HtmlSubmitInput button = (HtmlSubmitInput)form.getInputByValue("登 录");
        final HtmlTextInput textField = form.getInputByName("login");  
        final HtmlPasswordInput pass = form.getInputByName("password");  
        //4.给元素赋值  
        textField.click();
        textField.setValueAttribute("knshen");  
        pass.click();
        pass.setValueAttribute("sk530530");  
        //5.提交  
        HtmlPage page2 = hi.click();  
        System.out.println(page2.asXml());
        
	}
	
	public static void main(String[] args) throws Exception {
		//HtmlUnitDemo.loginGithub();;

	}

}

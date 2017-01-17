package edu.sjtu.jsearcher.demo;

import java.io.*;
import java.util.*;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MockLogin {
	private static CloseableHttpClient httpClient = HttpClients.createDefault();  
    private static HttpClientContext context = new HttpClientContext();  
    private static Header locationHeader = null;
    
	public static String sendGet(String url) {  
        CloseableHttpResponse response = null;  
        String content = null;  
        try {  
            HttpGet get = new HttpGet(url);  
            response = httpClient.execute(get, context);  
            HttpEntity entity = response.getEntity();  
            content = EntityUtils.toString(entity);  
            EntityUtils.consume(entity);  
            
           
            return content;  
        } catch (Exception e) {  
            e.printStackTrace();  
            if (response != null) {  
                try {  
                    response.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
        return content;  
    }  
  
    public static String sendPost(String url, List<NameValuePair> nvps) {  
        CloseableHttpResponse response = null;  
        String content = null;  
        try {  
            //　HttpClient中的post请求包装类  
            HttpPost post = new HttpPost(url);  
            // nvps是包装请求参数的list  
            if (nvps != null) {  
                post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));  
            }  
            // 执行请求用execute方法，content用来帮我们附带上额外信息  
            response = httpClient.execute(post, context);  
            // 得到相应实体、包括响应头以及相应内容  
            HttpEntity entity = response.getEntity();  
            // 得到response的内容  
            content = EntityUtils.toString(entity);  
            //　关闭输入流  
            locationHeader = response.getFirstHeader("Location");
            EntityUtils.consume(entity);  
            return content;  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (response != null) {  
                try {  
                    response.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
        return content;  
    }  
    
    private static Map<String, String> fetchCSDNParam(String url) throws IOException {  
    	Map<String, String> data = new HashMap<String, String>();
    	
    	String html = sendGet(url);  
        Document doc = Jsoup.parse(html);  
        Element form = doc.select(".user-pass").get(0);  
        String lt = form.select("input[name=lt]").get(0).val();  
        String execution = form.select("input[name=execution]").get(0).val();  
        String _eventId = form.select("input[name=_eventId]").get(0).val();  
       
        data.put("lt", lt);
        data.put("execution", execution);
        data.put("_eventId", _eventId);
        
        System.out.println("获取成功。。。。。"); 
        return data;
    }  
    
    public static boolean mockLogin(String url, String username, String password) throws Exception {  
    	System.out.println("开始登陆。。。"); 
        boolean result = false;  
        
        //Map<String, String> data = fetchCSDNParam(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
        //nvps.add(new BasicNameValuePair("username", username));  
        //nvps.add(new BasicNameValuePair("password", password));  
        //nvps.add(new BasicNameValuePair("lt", data.get("lt")));  
        //nvps.add(new BasicNameValuePair("execution", data.get("executio")));  
        //nvps.add(new BasicNameValuePair("_eventId", data.get("_eventId")));  
        //nvps.add(new BasicNameValuePair("id", "knshen"));
        //nvps.add(new BasicNameValuePair("pw", "530530"));
        //nvps.add(new BasicNameValuePair("submit", "login"));
        nvps.add(new BasicNameValuePair("email", username));
        nvps.add(new BasicNameValuePair("password", password));
        
        String ret = sendPost(url, nvps);
        String info = locationHeader.getValue();
        
        String welcome = MockLogin.sendGet(info);
        System.out.println(welcome);
        if (ret.indexOf("redirect_back") > -1) {  
        	System.out.println("登陆成功。。。。。");  
            result = true;  
        } else if (ret.indexOf("登录太频繁") > -1) {  
        	System.out.println("登录太频繁");
    	// get request to get form dataknshe    	
        } else {  
        	System.out.println("登陆失败。。。。。");  
        }  
        return result;  
    }  
	public static void main(String[] args) throws Exception {		
		String url = "http://www.renren.com/PLogin.do";
		
		MockLogin.mockLogin(url, "572451704@qq.com", "sk530530");
	}

}

package demo;

import java.util.*;
import java.io.*;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
	public static Map<String,String> cookieMap = new HashMap<String, String>(64);
    //从响应信息中获取cookie
	
	public static void printResponse(HttpResponse httpResponse) throws ParseException, IOException {
        // 获取响应消息实体
        HttpEntity entity = httpResponse.getEntity();
        // 响应状态
        System.out.println("status:" + httpResponse.getStatusLine());
        System.out.println("headers:");
        HeaderIterator iterator = httpResponse.headerIterator();
        while (iterator.hasNext()) {
            System.out.println("\t" + iterator.next());
        }
        // 判断响应实体是否为空
        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            System.out.println("response length:" + responseString.length());
            System.out.println("response content:"
                    + responseString.replace("\r\n", ""));
        }
    }

    
    
    public static String setCookie(HttpResponse httpResponse)
    {
        System.out.println("----setCookieStore");
        Header headers[] = httpResponse.getHeaders("Set-Cookie");
        if (headers == null || headers.length==0)
        {
            System.out.println("----there are no cookies");
            return null;
        }
        String cookie = "";
        for (int i = 0; i < headers.length; i++) {
            cookie += headers[i].getValue();
            if(i != headers.length-1)
            {
                cookie += ";";
            }
        }

        String cookies[] = cookie.split(";");
        for (String c : cookies)
        {
            c = c.trim();
            if(cookieMap.containsKey(c.split("=")[0]))
            {
                cookieMap.remove(c.split("=")[0]);
            }
            cookieMap.put(c.split("=")[0], c.split("=").length == 1 ? "":(c.split("=").length ==2?c.split("=")[1]:c.split("=",2)[1]));
        }
        System.out.println("----setCookieStore success");
        String cookiesTmp = "";
        for (String key :cookieMap.keySet())
        {
            cookiesTmp +=key+"="+cookieMap.get(key)+";";
        }

        return cookiesTmp.substring(0,cookiesTmp.length()-2);
    }
    
	public void login() throws Exception {
	    //创建一个HttpClient
        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        try {
            //创建一个get请求用来接收_xsrf信息
        	HttpGet get = new HttpGet("http://www.zhihu.com/");
            //获取_xsrf TODO Auto-generated method stub
            CloseableHttpResponse response = httpClient.execute(get);
            setCookie(response);
            String responseHtml = EntityUtils.toString(response.getEntity());
            String xsrfValue = responseHtml.split("<input type=\"hidden\" name=\"_xsrf\" value=\"")[1].split("\"/>")[0];
            System.out.println("xsrfValue:" + xsrfValue);
            response.close();
            
            //构造post数据
            List<NameValuePair> valuePairs = new LinkedList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("_xsrf", xsrfValue));
            valuePairs.add(new BasicNameValuePair("email", "572451704@qq.com"));
            valuePairs.add(new BasicNameValuePair("password", "sk530530"));
            valuePairs.add(new BasicNameValuePair("remember_me", "true"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);

            //创建一个post请求
            HttpPost post = new HttpPost("http://www.zhihu.com/login/email");
            post.setHeader("Cookie", " cap_id=\"YjA5MjE0YzYyNGQ2NDY5NWJhMmFhN2YyY2EwODIwZjQ=|1437610072|e7cc307c0d2fe2ee84fd3ceb7f83d298156e37e0\"; ");

            //注入post数据
            post.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(post);
            //打印登录是否成功信息
            printResponse(httpResponse);

            //构造一个get请求，用来测试登录cookie是否拿到
            HttpGet g = new HttpGet("http://www.zhihu.com/question/following");
            //得到post请求返回的cookie信息 TODO Auto-generated method stub
            String c = setCookie(httpResponse);
            //将cookie注入到get请求头当中
            g.setHeader("Cookie",c);
            CloseableHttpResponse r = httpClient.execute(g);
            String content = EntityUtils.toString(r.getEntity());
            System.out.println(content);
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		
        
	}
	
	public void get() {  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        try {  
            // 创建httpget. 
            HttpGet httpget = new HttpGet("https://leetcode.com/problemset/algorithms/");  
            System.out.println("executing request " + httpget.getURI());  
            // 执行get请求.    
            CloseableHttpResponse response = httpclient.execute(httpget);  
            try {  
                // 获取响应实体    
                HttpEntity entity = response.getEntity();  
                System.out.println("--------------------------------------");  
                // 打印响应状态    
                System.out.println(response.getStatusLine());  
                if (entity != null) {  
                    // 打印响应内容长度    
                    System.out.println("Response content length: " + entity.getContentLength());  
                    // 打印响应内容    
                    System.out.println("Response content: " + EntityUtils.toString(entity));  
                    // 编码
                    System.out.println("Response encoding: " + entity.getContentEncoding());
                }  
                System.out.println("------------------------------------");  
            } finally {  
                response.close();  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // 关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
	
	public void parse() {
        Document doc = null;
        try {
        	long a = System.currentTimeMillis();
            doc = Jsoup.connect("http://42.62.30.180").get();
            long b = System.currentTimeMillis();
            System.out.println((b-a) + "ms");
            
            
            System.out.println("#" + doc.baseUri());
            Elements links = doc.select("a[href]");
            Elements media = doc.select("[src]");
            Elements imports = doc.select("link[href]");
            
            for(Element link : links) {
            	System.out.println(link.tagName());
            	System.out.println(link.attr("abs:href"));
            	System.out.println(link.text());
            	System.out.println("-----------------------");
            }
            
            /*
            for(Element m : media) { 	
            	System.out.println(m.tagName());
            	System.out.println(m.attr("abs:src"));
            	System.out.println("-----------------------");
            	
            }
            */
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

	}
	
	public static void main(String[] args) throws Exception {
		Main m = new Main();
		//m.get();
		m.parse();
		//202.120.2.119
	}

}

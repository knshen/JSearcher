package parser.sjtu.sk;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;
import downloader.sjtu.sk.HtmlDownloader;
import logging.sjtu.sk.Logging;
import url.manager.sjtu.sk.URL;

public class HtmlParser {
	private Document doc = null;
	
	public Document getDocument() {
		return this.doc;
	}
	
	public List<URL> parse(String html) {
		return parse(html, "");
	}
	
	public List<URL> parse(String html, String base_url) {
		doc = Jsoup.parse(html, base_url);
		//doc = Jsoup.connect("https://leetcode.com/problemset/algorithms/").get();
		Elements links = doc.select("a[href]");
		
		List<URL> res = new ArrayList<URL>();
		
		for(Element link : links) {
			String info = link.attr("abs:href") + "\n"
					+ link.text() + "\n"
					+ "-----------------------";
			//Logging.log(info);
			URL url = new URL(link.attr("abs:href"));
			res.add(url);
			
		}
		return res;
	}
	
	public static void main(String[] args) {
		HtmlParser hp = new HtmlParser();
		HtmlDownloader hd = new HtmlDownloader();
		URL url = new URL("https://leetcode.com/problemset/algorithms/");
		hp.parse(hd.download(url), url.getURLValue());
	}

}

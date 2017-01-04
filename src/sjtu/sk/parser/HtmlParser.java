package sjtu.sk.parser;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

import sjtu.sk.downloader.HtmlDownloader;
import sjtu.sk.logging.Logging;
import sjtu.sk.url.manager.Link;
import sjtu.sk.url.manager.URL;

/**
 * Use JSoup to parse htmls
 * @author ShenKai
 *
 */
public class HtmlParser {
	public List<URL> parse(String html) {
		return parse(html, "");
	}
	
	public Document getDocument(String html, String base_url) {
		return Jsoup.parse(html, base_url);
	}
	/**
	 * extract url links in the html, also save the Document object
	 * @param html : html string
	 * @param base_url : the URL w.r.t the html string
	 * @return new url links in the html 
	 */
	public List<URL> parse(String html, String base_url) {
		Document doc = Jsoup.parse(html, base_url);
		//doc = Jsoup.connect("https://leetcode.com/problemset/algorithms/").get();
		Elements links = doc.select("a[href]");
		
		List<URL> res = new ArrayList<URL>();
		
		for(Element link : links) {
			String info = link.attr("abs:href") + "\n"
					+ link.text() + "\n"
					+ "-----------------------";
			//Logging.log(info);
			Link lin = new Link(link.attr("abs:href"), link.text().trim());
			
			res.add(lin);
		}
		return res;
	}
	
	public static void main(String[] args) {
		HtmlParser hp = new HtmlParser();
		HtmlDownloader hd = new HtmlDownloader();
		URL url = new URL("https://leetcode.com/problemset/algorithms/");
		System.out.println(hp.parse(hd.download(url), url.getURLValue()));
	}

}

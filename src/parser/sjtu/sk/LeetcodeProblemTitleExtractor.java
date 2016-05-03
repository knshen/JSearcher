package parser.sjtu.sk;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LeetcodeProblemTitleExtractor extends DataExtractor {
	
	public List<String> extract(Document doc) {
		List<String> res = new ArrayList<String>();
		if(doc == null)
			return null;
		
		Elements eles = doc.select(".question-title");
		for(Element ele : eles) 
			res.add(ele.select("h3").text().trim());
		
		return res;
	}
	
	
	public static void main(String[] args) throws Exception {
		//unit test
		DataExtractor de = new LeetcodeProblemTitleExtractor();
		
		Document doc = Jsoup.connect("https://leetcode.com/problems/top-k-frequent-elements/").get();
		de.extract(doc);
	}

}

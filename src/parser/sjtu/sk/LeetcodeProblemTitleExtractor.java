package parser.sjtu.sk;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dto.user.LeetCodeTitleDTO;

public class LeetcodeProblemTitleExtractor extends DataExtractor {
	
	public List<Object> extract(Document doc) {
		List<Object> res = new ArrayList<Object>();
		if(doc == null)
			return null;
		
		Elements eles = doc.select(".question-title");
		for(Element ele : eles) {
			String str = ele.select("h3").text().trim();
			String tmp[] = str.split("\\.");
			if(tmp.length < 2)
				continue;
			LeetCodeTitleDTO dto = new LeetCodeTitleDTO(Integer.parseInt(tmp[0].trim()), tmp[1].trim());
			res.add(dto);
		}
			
		return res;
	}
	
	
	public static void main(String[] args) throws Exception {
		//unit test
		DataExtractor de = new LeetcodeProblemTitleExtractor();
		
		Document doc = Jsoup.connect("https://leetcode.com/problems/top-k-frequent-elements/").get();
		de.extract(doc);
	}

}

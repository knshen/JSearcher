package sjtu.sk.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dto.user.LeetCodeProblemDTO;
import dto.user.LeetCodeTitleDTO;

public class LeetcodeProblemExtractor extends DataExtractor {
	public List<Object> extract(Document doc) {
		List<Object> res = new ArrayList<Object>();
		if(doc == null)
			return null;
		
		int proID = -1;
		//question-title
		Elements eles = doc.select(".question-title");
		for(Element ele : eles) {
			String str = ele.select("h3").text().trim();
			String tmp[] = str.split("\\.");
			if(tmp.length < 2)
				continue;
			proID = Integer.parseInt(tmp[0].trim());
		}
		
		//question-content
		eles = doc.select(".question-content");
		for(Element ele : eles) {
			String str = ele.text().trim();
			int index = str.indexOf("Subscribe to");
			str = str.substring(0, index);
			//System.out.println(str);
			LeetCodeProblemDTO dto = new LeetCodeProblemDTO(proID, str);
			res.add(dto);
		}
		return res;
	}
	
	public static void main(String[] args) throws Exception {
		DataExtractor de = new LeetcodeProblemExtractor();
		Document doc = Jsoup.connect("https://leetcode.com/problems/longest-absolute-file-path/").get();
		de.extract(doc);
	}

}

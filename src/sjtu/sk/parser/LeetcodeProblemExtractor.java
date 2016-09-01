package sjtu.sk.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dto.user.LeetcodeProblemDTO;

public class LeetcodeProblemExtractor extends DataExtractor {
	public List<Object> extract(Document doc, String url) {
		List<Object> res = new ArrayList<Object>();
		if(doc == null)
			return null;
		
		boolean isSet = false;
		LeetcodeProblemDTO dto = new LeetcodeProblemDTO();
		dto.setUrl(url); // url
		
		//question id & title
		Elements eles = doc.select(".question-title");
		for(Element ele : eles) {
			String str = ele.select("h3").text().trim();
			String tmp[] = str.split("\\.");
			if(tmp.length < 2)
				continue;
			int proID = Integer.parseInt(tmp[0].trim());
			String title = tmp[1].trim();
			dto.setId(proID);
			dto.setTitle(title);
			isSet = true;
		}
		
		
		//question info
		eles = doc.select(".question-info");
		for(Element ele : eles) {
			Elements lis = ele.select("ul").select("li");
			for(Element li : lis) {
				if(li.text().indexOf("Accept") != -1) {
					long accept = Long.parseLong(li.text().split(":")[1].trim());
					dto.setAccept(accept);
				}
				if(li.text().indexOf("Submiss") != -1) {
					long submit = Long.parseLong(li.text().split(":")[1].trim());
					dto.setSubmission(submit);
				}
				if(li.text().indexOf("Diff") != -1) {
					String diff = li.text().split(":")[1].trim();
					dto.setDifficulty(diff);
				}
			}
		}
		
		
		//question content
		eles = doc.select(".question-content");
		for(Element ele : eles) {
			String str = ele.text().trim();
			int index = str.indexOf("Subscribe to");
			str = str.substring(0, index);
			dto.setContent(str);
		}
		
		if(isSet)
			res.add(dto);
		
		return res;
	}
	
	public static void main(String[] args) throws Exception {
		DataExtractor de = new LeetcodeProblemExtractor();
		
		String url = "https://leetcode.com/problems/find-the-difference/";
		Document doc = Jsoup.connect(url).get();
		System.out.println(de.extract(doc, url));
	}

}

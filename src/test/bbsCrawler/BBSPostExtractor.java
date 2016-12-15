package test.bbsCrawler;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sjtu.sk.parser.DataExtractor;

public class BBSPostExtractor extends DataExtractor{
	public List<Object> extract(Document doc, String url) {
		List<Object> res = new ArrayList<Object>();
		BBSPostDTO dto = new BBSPostDTO();
		dto.setUrl(url);
		boolean isSet = false;
		
		Elements eles = doc.select("pre");
		if(eles.size() > 0) {
			Element ele = eles.get(0);
			String tmp[] = ele.text().trim().split("\n");		
			String content = "";
			
			for(String str : tmp) {
				if(str.startsWith("发信人"))
					continue;
				if(str.startsWith("标  题:")) {
					dto.setTitle(str.trim());
					if(str.indexOf("实习") != -1)
						dto.setIsIntern(true);
					else
						dto.setIsIntern(false);
				}
				else if(str.startsWith("发信站:")) {
					int start = str.indexOf("(") + 1;
					int end = str.indexOf("日") + 1;
					dto.setDate(str.substring(start, end).trim());
				}
				else 
					content += str;
			}
			
			if(!content.equals("")) {
				isSet = true;
				dto.setContent(content.trim());
			}
		}
			
		if(isSet) 
			res.add(dto);
		
		return res;
	}
	
	public static void main(String[] args) throws Exception {
		DataExtractor de = new BBSPostExtractor();
		//String url = "https://bbs.sjtu.edu.cn/bbsdoc,board,JobInfo,page,6174.html";
		String url = "https://bbs.sjtu.edu.cn/bbscon,board,JobInfo,file,M.1472954311.A.html";
		Document doc = Jsoup.connect(url).get();
		
		System.out.println(de.extract(doc, url));

	}

}

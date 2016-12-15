package test.hduCrawler;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sjtu.sk.logging.Logging;
import sjtu.sk.parser.DataExtractor;
import test.dto.OJDTO;

public class HDUProblemExtractor extends DataExtractor {
	public List<Object> extract(Document doc, String url) {
		List<Object> res = new ArrayList<Object>();
		
		OJDTO dto = new OJDTO();
		dto.setUrl(url);
		int id = Integer.parseInt(url.split("=")[1]);
		dto.setId(id);
		boolean isNull = true;
		
		Elements eles = doc.select("h1");
		if(eles.size() > 0) {
			String title = eles.get(0).text().trim();
			dto.setTitle(title);
		}
		
		try {
			eles = doc.select("span");
			if(eles.size() > 0) {
				String text = eles.get(0).text();
				//System.out.println(text);
				int from = text.indexOf(":", text.indexOf("Total Submission"));
				int to = text.indexOf("Accepted Submission");
				String str = text.substring(from+1, to).trim();
				str = str.substring(0, str.length()-4);
				long submit = Long.parseLong(str); 
				dto.setSubmit(submit);
				
				from = text.indexOf(":", to);
				long accept = Long.parseLong(text.substring(from+1).trim());
				dto.setAccept(accept);
				isNull = false;
			}
			
			eles = doc.select("td[align=center]");
			eles = eles.get(2).select("div");
			
			String content = "";
			for(Element ele : eles) 
				content += ele.text() + "\n";
			
			dto.setContent(content);
			
			if(!isNull)
				res.add(dto);

		} catch(Exception e) {
			Logging.log("data parse is not successful!\n");
			return res;
		}
		
		return res;
	}
	
	public static void main(String args[]) throws Exception {
		DataExtractor de = new HDUProblemExtractor();
		String url = "http://acm.hdu.edu.cn/showproblem.php?pid=1000";
		Document doc = Jsoup.connect(url).get();
		System.out.println(de.extract(doc, url));
	}
}

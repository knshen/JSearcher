package sjtu.sk.parser;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dto.user.OJDTO;

public class POJProblemExtractor extends DataExtractor {
	public List<Object> extract(Document doc, String url) {
		List<Object> res = new ArrayList<Object>();
		
		OJDTO dto = new OJDTO();
		dto.setUrl(url);
		int id = Integer.parseInt(url.split("=")[1]);
		dto.setId(id);
		boolean isSet = true;
		
		Elements eles = doc.select(".ptt");
		if(eles.size() == 0)
			isSet = false;
		else {
			for(Element ele : eles) {
				dto.setTitle(ele.text().trim());
			}
		}

		eles = doc.select(".plm");
		if(eles.size() == 0)
			isSet = false;
		else {
			for(Element ele : eles.get(0).select("td")) {
				if(ele.text().indexOf("Submission") != -1) {
					long submit = Long.parseLong(ele.text().split(":")[1].trim());
					dto.setSubmit(submit);
				}
				if(ele.text().indexOf("Accept") != -1) {
					long accept = Long.parseLong(ele.text().split(":")[1].trim());
					dto.setAccept(accept);
				}
			}
		}
		
		
		Elements titles = doc.select(".pst");
		Elements content = doc.select(".ptx");
		Elements in_out = doc.select(".sio");
		if(titles.size() == 0 || content.size() == 0 || in_out.size() == 0)
			isSet = false;
		else {
			int i = 0;
			int j = 0;
			String str = "";
			for(Element ele : titles) {
				if(ele.text().indexOf("Sample Input") != -1) {
					str += ele.text().trim() + "\n" + in_out.get(j++).text().trim() + "\n";
				}
				else if(ele.text().indexOf("Sample Output") != -1) {
					str += ele.text().trim() + "\n" + in_out.get(j++).text().trim() + "\n";
				}	
				else if(ele.text().indexOf("Description") != -1) {
					str += ele.text().trim() + "\n" + content.get(i++).text().trim() + "\n";
				}
				else if(ele.text().indexOf("Input") != -1) {
					str += ele.text().trim() + "\n" + content.get(i++).text().trim() + "\n";
				}
				else if(ele.text().indexOf("Output") != -1) {
					str += ele.text().trim() + "\n" + content.get(i++).text().trim() + "\n";
				}
				
				else if(ele.text().indexOf("Source") != -1) {
					str += ele.text().trim() + "\n" + content.get(i++).text().trim() + "\n";
				}
			}
			
			dto.setContent(str);
		}

		if(isSet)
			res.add(dto);
		
		return res;
	}
	
	public static void main(String[] args) throws Exception {
		DataExtractor de = new POJProblemExtractor();
		String url = "http://poj.org/problem?id=3936";
		Document doc = Jsoup.connect(url).get();
		System.out.println(de.extract(doc, url));

	}

}

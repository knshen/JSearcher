package nba.player;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import edu.sjtu.jsearcher.parser.DataExtractor;
import edu.sjtu.jsearcher.test.dto.PlayerDTO;

public class PlayerExtractor extends DataExtractor {
	public List<Object> extract(Document doc, String url) {
		String team = doc.select("strong").text();
		
		List<Object> res = new ArrayList<Object>();
		Element $table = doc.select("table[width=\"702\"]").get(0);
		List<Element> $trs = $table.select("tr");
		
		for(int i=1; i<$trs.size(); i++) {
			List<Element> $tds = $trs.get(i).select("td");
			PlayerDTO dto = new PlayerDTO();
			
			dto.setTeam(team);
			dto.setNumber($tds.get(0).text());
			dto.setName($tds.get(1).text());
			dto.setPosition($tds.get(2).text());
			
			String heightText = $tds.get(3).text();
			double height = Double.parseDouble(heightText.substring(0, heightText.indexOf("米")));
			dto.setHeight(height);
			
			String weightText = $tds.get(4).text();
			double weight = Double.parseDouble(weightText.substring(0, weightText.indexOf("公斤")));
			dto.setWeight(weight);
			
			dto.setBirthday($tds.get(6).text());
			
			String careerText = $tds.get(7).text();
			int career = Integer.parseInt(careerText.substring(0, careerText.indexOf("年")));
			dto.setCareerAge(career);
			res.add(dto);
		}
		return res;
	}
	
	public static void main(String args[]) throws Exception {
		DataExtractor extractor = new PlayerExtractor(); 
		String url = "http://nba.sports.sina.com.cn/team/Spurs.shtml";
		Document doc = Jsoup.connect(url).get();
		List<Object> res = extractor.extract(doc, url);
		System.out.println(res);
	}
}

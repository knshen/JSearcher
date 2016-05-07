package parser.sjtu.sk;

import java.util.*;
import java.io.*;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import logging.sjtu.sk.Logging;

public class ImageExtractor extends DataExtractor {
	public List<Object> extract(Document doc) {
		List<Object> res = new ArrayList<Object>();
		
		Elements images = doc.select("img[src]");
		int id = 1;
		for(Element image : images) { 
			String urlPath = image.attr("src");
			Logging.log("download image: " + urlPath);
			String savePath = "/home/knshen/imageData/image" + (id++) + ".jpg";
			downloadImage(urlPath, savePath);
		}
		return res;
	}
	
	public boolean downloadImage(String urlPath, String savePath) {
		try {
			Connection conn = Jsoup.connect(urlPath).ignoreContentType(true);		 
			Response response = conn.execute();
			if(response.statusCode() != 404) {
				byte data[] = response.bodyAsBytes();
				FileOutputStream outputStream = new FileOutputStream(savePath);
	            outputStream.write(data);
	            outputStream.close();
			}	
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return false;
	}
	
	public static void main(String[] args) throws IOException {
		DataExtractor de = new ImageExtractor();
		Document doc = Jsoup.connect("http://sports.qq.com/nba/").get();
		de.extract(doc);
	}

}

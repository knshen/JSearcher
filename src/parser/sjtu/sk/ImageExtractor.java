package parser.sjtu.sk;

import java.util.*;
import java.io.*;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import util.sjtu.sk.OperatingSystem;
import util.sjtu.sk.Util;
import logging.sjtu.sk.Logging;

public class ImageExtractor extends DataExtractor {
	public List<Object> extract(Document doc) {
		List<Object> res = new ArrayList<Object>();
		
		Elements images = doc.select("img[src]");
	
		for(Element image : images) { 
			String urlPath = image.attr("src");
			Logging.log("download image: " + urlPath);
			String savePath = Util.MD5(urlPath);
			if(Util.getCurrentOS() == OperatingSystem.LINUX) 
				savePath = "imageData/" + savePath + ".jpg";
			
			else if(Util.getCurrentOS() == OperatingSystem.WINDOWS) 
				savePath = "f://imageData//" + savePath + ".jpg";
			
			if(downloadImage(urlPath, savePath))
				res.add(savePath);
		}
		return res;
	}
	
	/**
	 * Use JSoup to download image file according to URL
	 * @param urlPath : image URL
	 * @param savePath : image save path (on disk)
	 * @return : if succeed?
	 */
	private boolean downloadImage(String urlPath, String savePath) {
		FileOutputStream outputStream = null;
		try {
			Connection conn = Jsoup.connect(urlPath).ignoreContentType(true);		 
			Response response = conn.execute();
			if(response.statusCode() != 404) {
				byte data[] = response.bodyAsBytes();
				outputStream = new FileOutputStream(savePath);
	            outputStream.write(data);
	            
	            return true;
			}	
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			try {
				outputStream.close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException {
		DataExtractor de = new ImageExtractor();
		Document doc = Jsoup.connect("http://sports.qq.com/nba/").get();
		de.extract(doc);
	}

}

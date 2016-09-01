package sjtu.sk.parser;

import java.util.*;
import java.io.*;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dto.user.Picture;
import sjtu.sk.logging.Logging;
import sjtu.sk.util.OperatingSystem;
import sjtu.sk.util.Util;

public class ImageExtractor extends DataExtractor {
	public List<Object> extract(Document doc, String url) {
		List<Object> res = new ArrayList<Object>();
		
		Elements images = doc.select("img[src]");
	
		for(Element image : images) { 
			String urlPath = image.attr("src");
			//Logging.log("download image: " + urlPath);
			String savePath = Util.MD5(urlPath);
			if(Util.getCurrentOS() == OperatingSystem.LINUX) 
				savePath = "imageData/" + savePath + ".jpg";
			
			else if(Util.getCurrentOS() == OperatingSystem.WINDOWS) 
				savePath = "imageData/" + savePath + ".jpg";
			
			if(downloadImage(urlPath, savePath)) {
				Picture pic = new Picture(savePath, urlPath);
				res.add(pic);
			}
				
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
		if(!Util.isURLLegal(urlPath))
			return false;
		
		urlPath = urlPath.trim();
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
				if(outputStream != null) 
					outputStream.close();
								
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException {
		DataExtractor de = new ImageExtractor();
		
		String url = "http://sports.qq.com/nba/";
		Document doc = Jsoup.connect(url).get();
		de.extract(doc, url);
	}

}

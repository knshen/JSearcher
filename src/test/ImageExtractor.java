package test;

import java.util.*;
import java.io.*;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sjtu.sk.logging.Logging;
import sjtu.sk.parser.DataExtractor;
import sjtu.sk.util.OperatingSystem;
import sjtu.sk.util.Util;
import test.dto.Picture;

public class ImageExtractor extends DataExtractor {
	
	public List<Object> extract(Document doc, String url) {
		// The parameter url must be the parent url of image file url
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
			
			if(Util.downloadBinary(urlPath, savePath)) {
				Picture pic = new Picture(savePath, urlPath);
				res.add(pic);
			}
				
		}
		return res;
	}
	
	
	public static void main(String[] args) throws IOException {
		DataExtractor de = new ImageExtractor();
		
		String url = "http://sports.qq.com/nba/";
		Document doc = Jsoup.connect(url).get();
		de.extract(doc, url);
	}

}

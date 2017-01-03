package test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sjtu.sk.parser.DataExtractor;
import sjtu.sk.util.Util;
import test.dto.PDF;

public class PDFExtractor extends DataExtractor{
	public List<Object> extract(Document doc, String url) {
		// The parameter url must be the parent url of image file url
		List<Object> res = new ArrayList<Object>();
		Elements pdfs = doc.select("a[name=FullTextPDF]");
		int id = 1;
		for(Element ele : pdfs) {
			String rs_path = ele.attr("abs:href");
			System.out.println(rs_path);
			Util.downloadBinary(rs_path, "f://ASE2016//"+id+".pdf");
			PDF pdf = new PDF();
			pdf.setUrl(rs_path);
			pdf.setFilePath("f://ASE2016//"+id+".pdf");
			res.add(pdf);
			id++;
		}
		
		return res;
	}
	
	public static void main(String[] args) throws Exception {
		DataExtractor de = new PDFExtractor();
		String url = "http://dl.acm.org/citation.cfm?id=2970276&CFID=884347639&CFTOKEN=69104841";
		Document doc = Jsoup.connect(url).get();
		de.extract(doc, url);
		
	}

}

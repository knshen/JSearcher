package edu.sjtu.jsearcher.util;

import java.io.*;
import java.util.*;

import org.jdom2.Document;  
import org.jdom2.Element;  
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;  

public class XMLHandler {
	/**
	 * read mysql table configuration file (this configuration is needed only when the persistent
	 * style is MySQL)
	 * @param filePath
	 * @param db
	 * @param table
	 * @return
	 */
	public static Map<String, List<String>> readDBConfig(String filePath, String db, String table) {
		Map<String, List<String>> res = new HashMap<String, List<String>>();
		List<String> col_names = new ArrayList<String>();
		List<String> col_types = new ArrayList<String>();
		
		try {
			SAXBuilder builder = new SAXBuilder();  
	        Document document = builder.build(filePath);  
	        Element root = document.getRootElement();  
	        List<Element> items = root.getChildren();
	        for(Element db_ele : items) {
	        	if(db_ele.getAttributeValue("name").equals(db)) {
	        		items = db_ele.getChildren();
	        		break;
	        	}
	        }
	        
	        for(Element table_ele : items) {
	        	if(table_ele.getAttributeValue("name").equals(table)) {
	        		items = table_ele.getChildren();
	        		break;
	        	}
	        }
	        
	        for(Element col_ele : items) {
	        	col_names.add(col_ele.getValue().split(" ")[0]);
	        	col_types.add(col_ele.getValue().split(" ")[1]);
	        }
	        
	        res.put("names", col_names);
	        res.put("types", col_types);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(JDOMException jde) {
			jde.printStackTrace();
		}
		return res;
	}
	
	public static void main(String[] args) {
		//System.out.println(XMLReader.readClusterConfig("cluster.xml"));
		//System.out.println(XMLHandler.readSchedulerConfig("scheduler.xml"));
		System.out.println(XMLHandler.readDBConfig("db.xml", "JSearcher", "test"));
	}

}

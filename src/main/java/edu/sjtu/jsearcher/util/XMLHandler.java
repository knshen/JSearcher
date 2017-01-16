package edu.sjtu.jsearcher.util;

import java.io.*;
import java.util.*;

import org.jdom2.Document;  
import org.jdom2.Element;  
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;  
import org.jdom2.output.Format;  
import org.jdom2.output.XMLOutputter;  

import edu.sjtu.jsearcher.balance.Node;

public class XMLHandler {
	
	/**
	 * write data to a xml file
	 * we now suppose data do not have hierarchical structures
	 * @param data
	 * @param fileName
	 */
	private static void writeXML(String theme, Map<String, Object> data, String filePath) {
        Element root = new Element(theme);  
        Document document = new Document(root);  
  
        for(Map.Entry<String, Object> entry : data.entrySet()) {
        	Element ele = new Element(entry.getKey());  
        	ele.setText(entry.getValue().toString());
        	root.addContent(ele);
        }
        
        XMLOutputter XMLOut = new XMLOutputter();  
        try {  
            Format f = Format.getPrettyFormat();  
            f.setEncoding("UTF-8");//default=UTF-8  
            XMLOut.setFormat(f);  
            XMLOut.output(document, new FileOutputStream(filePath));  
        } catch (IOException ioe) {  
            ioe.printStackTrace();  
        }  

	}
	
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

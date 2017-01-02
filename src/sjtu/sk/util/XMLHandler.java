package sjtu.sk.util;

import java.io.*;
import java.util.*;

import org.jdom2.Document;  
import org.jdom2.Element;  
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;  
import org.jdom2.output.Format;  
import org.jdom2.output.XMLOutputter;  

import sjtu.sk.balance.Node;

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
	 * get cluster info
	 * @param filePath
	 * @return
	 */
	public static List<Node> readClusterConfig(String filePath) {
		List<Node> nodes = new ArrayList<Node>();
		/**
		 * <node>
		 *   <node_id>...</node_id>
		 *   <node_ip>...</node_ip>
		 */
		try {
			SAXBuilder builder = new SAXBuilder();  
	        Document document = builder.build(filePath);  
	        Element root = document.getRootElement();  
	        List<Element> node_eles = root.getChildren();

	        for(Element node_ele : node_eles) {
	        	Element name = node_ele.getChild("name");
	        	Element ip = node_ele.getChild("ip");
	        	Node node = new Node(name.getValue(), ip.getValue());
	        	nodes.add(node);
	        }
	        
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(JDOMException jde) {
			jde.printStackTrace();
		}
		return nodes;
	}
	
	public static Map<String, Object> readSchedulerConfig(String filePath) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			SAXBuilder builder = new SAXBuilder();  
	        Document document = builder.build(filePath);  
	        Element root = document.getRootElement();  
	        List<Element> items = root.getChildren();
	        
	        for(Element item : items) {
	        	map.put(item.getName(), item.getValue());
	        }
	        
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(JDOMException jde) {
			jde.printStackTrace();
		}
		return map;
	}
	
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

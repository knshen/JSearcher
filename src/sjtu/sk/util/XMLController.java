package sjtu.sk.util;

import java.io.*;
import java.util.*;

import org.jdom2.Document;  
import org.jdom2.Element;  
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;  
import org.jdom2.output.Format;  
import org.jdom2.output.XMLOutputter;  

public class XMLController {
	
	/**
	 * write data to a xml file
	 * we now suppose data do not have hierarchical structures
	 * @param data
	 * @param fileName
	 */
	public static void writeXML(Map<String, Object> data, String filePath) {
        Element root = new Element("JSearcherGlobalConfig");  
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
	 * parse an xml file
	 * we now suppose the xml file only has two levels
	 * @param filePath
	 * @return
	 */
	public static Map<String, Object> readXML(String filePath) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			SAXBuilder builder = new SAXBuilder();  
	        Document document = builder.build(filePath);  
	        Element root = document.getRootElement();  
	        List<Element> eles = root.getChildren();

	        for(Element ele : eles) {
	        	data.put(ele.getName(), ele.getValue());
	        }
	        
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(JDOMException jde) {
			jde.printStackTrace();
		}
		return data;
	}
	
	public static void main(String[] args) {
		//XMLController.writeXML(data, "f://a.xml");
		System.out.println(XMLController.readXML("f://a.xml"));
	}

}

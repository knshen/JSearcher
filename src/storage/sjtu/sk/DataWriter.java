package storage.sjtu.sk;

import java.text.SimpleDateFormat;
import java.util.*;

import url.manager.sjtu.sk.URL;

public class DataWriter {
	
	public static void writeData2DB(List<Object> data, String col_name, String dto) {
		DBController dc = DBController.createDBController("localhost", 27017);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date now = new Date();	
		col_name = "data" + dateFormat.format(now) + col_name;
		
		//for simplicity, drop table if the table exists before insert
		dc.removeAll(col_name);
		dc.createCol(col_name);
		
		dc.insert(col_name, data, dto);
	}
	
	public static void writeData2ES(List<Object> data, String task_name, String dto) {
		IndexController ec = IndexController.createIndexControllerInstance("localhost");
		String index = task_name.split("-")[0];
		String type = task_name.split("-")[1];
		
		
		if(ec.count(index, type) > 0) {
			ec.delete(index, type);
			ec.delete(index, type);
		} 
			
		ec.insert(data, index, type, dto);
	}
	
	public static void writeVisitedURL2DB(List<URL> urls) {
		DBController dc = DBController.createDBController("localhost", 27017);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date now = new Date();	
		String col_name = "visitedURL" + dateFormat.format(now);
		
		//for simplicity, drop table if the table exists before insert
		dc.removeAll(col_name);
		dc.createCol(col_name);
		
		List<Object> data = new ArrayList<Object>();
		for(URL url : urls)
			data.add(url);
		dc.insert(col_name, data, "url.manager.sjtu.sk.URL");
	}

	public static void main(String args[]) {
		List<URL> list = new ArrayList<URL>();
		list.add(new URL("http://www.baidu.com"));
		list.add(new URL("http://www.2345.com"));
		list.add(new URL("http://www.sjtu.edu.cn"));
		DataWriter.writeVisitedURL2DB(list);
	}
}

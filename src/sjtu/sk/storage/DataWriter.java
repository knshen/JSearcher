package sjtu.sk.storage;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;
import sjtu.sk.scheduler.SpiderConfig;
import sjtu.sk.url.manager.URL;
import sjtu.sk.util.Util;
import sjtu.sk.util.XMLHandler;

public class DataWriter {
	
	public static void writeData2MongoDB(List<Object> data, String col_name, String dto) {
		MongoDBController dc = MongoDBController.createDBController(SpiderConfig.storage_host, SpiderConfig.storage_port, SpiderConfig.db_name);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date now = new Date();	
		col_name = "data" + dateFormat.format(now) + col_name;
		
		//for simplicity, drop table if the table exists before insert
		dc.removeAll(col_name);
		dc.createCol(col_name);
		
		dc.insert(col_name, data, dto);
	}
	
	public static void writeData2MySQL(List<Object> data, String table, String dto) {
		MySQLController mc = MySQLController.createDBController(SpiderConfig.storage_host, SpiderConfig.storage_port, SpiderConfig.db_user, SpiderConfig.db_password, SpiderConfig.db_name);
		Map<String, List<String>> res = XMLHandler.readDBConfig("db.xml", "JSearcher", table);
		
		//mc.removeAll(table);// delete all before insert
		mc.insert(table, data, dto, res.get("names"), res.get("types"));
	}
	
	public static void writeData2ES(List<Object> data, String task_name, String dto) {
		IndexController ec = IndexController.createIndexControllerInstance(SpiderConfig.storage_host, SpiderConfig.storage_port, SpiderConfig.ES_cluster_name);
		String index = task_name.split("-")[0];
		String type = task_name.split("-")[1];
		
		/*
		if(ec.isIndexExist(index) && ec.isTypeExist(index, type) && ec.count(index, type) > 0) {
			Logging.log("delete before insert!\n");
			ec.delete(index, type);
		}*/
		
		ec.insert(data, index, type, dto);
	}
	
	// not applied yet
	public static void writeVisitedURL2DB(List<URL> urls) {
		MongoDBController dc = MongoDBController.createDBController(SpiderConfig.storage_host, SpiderConfig.storage_port, SpiderConfig.db_name);
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
		//TestDTO dto = new TestDTO();
		//dto.age = 33;
		//dto.id = 5;
		//dto.name = "Jerry";
		//DataWriter.writeData2MySQL(Arrays.asList(dto), "test", "sjtu.sk.storage.TestDTO");
	}
}

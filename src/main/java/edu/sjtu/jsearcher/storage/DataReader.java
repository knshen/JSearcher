package edu.sjtu.jsearcher.storage;

import java.text.SimpleDateFormat;
import java.util.*;

import edu.sjtu.jsearcher.scheduler.SpiderConfig;
import edu.sjtu.jsearcher.url.manager.URL;
import edu.sjtu.jsearcher.util.Triple;
import edu.sjtu.jsearcher.util.XMLHandler;

public class DataReader {
	public static List<Object> readDataFromMongoDB(Date date, String col_name, String dto, List<List<Triple>> filter, List<String> keys) {
		MongoDBController dc = MongoDBController.createDBController(SpiderConfig.storage_host, SpiderConfig.storage_port, SpiderConfig.db_name);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		col_name = "data" + dateFormat.format(date) + col_name;
		
		if((keys == null || keys.size() == 0) && 
				(filter == null || filter.size() == 0)) {
			return dc.queryAll(col_name, dto);
		}
		return dc.queryByWhere(col_name, dto, filter, keys);
		
	}
	
	public static List<Object> readDataFromES(String task_name, String dto) {
		String index = task_name.split("-")[0];
		String type = task_name.split("-")[1];
		
		IndexController ec = IndexController.createIndexControllerInstance(SpiderConfig.storage_host, SpiderConfig.storage_port, SpiderConfig.ES_cluster_name);
		
		return ec.searchAll(index, type, dto);	
	}
	
	public static List<Object> readDataFromMySQL(String table, String dto) {
		MySQLController mc = MySQLController.createDBController(SpiderConfig.storage_host, SpiderConfig.storage_port, SpiderConfig.db_user, SpiderConfig.db_password, SpiderConfig.db_name);
		Map<String, List<String>> res = XMLHandler.readDBConfig("db.xml", "JSearcher", table);
		return mc.queryAll(table, dto, res.get("names"), res.get("types"));
	}
	
	public static List<URL> readVisitedURLFromDB(Date date) {
		List<URL> res = new ArrayList<URL>();
		
		MongoDBController dc = MongoDBController.createDBController(SpiderConfig.storage_host, SpiderConfig.storage_port, SpiderConfig.db_name);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String col_name = "visitedURL" + dateFormat.format(date);
		
		List<Object> list = dc.queryAll(col_name, "url.manager.sjtu.sk.URL");
		for(Object obj : list) 
			res.add((URL)obj);
		
		return res;
	}
	
	public static void main(String args[]) {
		//DBReader.readDataFromDB(new Date(), "leetcodeProblemTitles", "dto.user.LeetCodeTitleDTO", null, null);
		//DataReader.readVisitedURLFromDB(new Date());
		List<Object> res = DataReader.readDataFromMySQL("test", "sjtu.sk.storage.TestDTO");
		System.out.println();
	}
}

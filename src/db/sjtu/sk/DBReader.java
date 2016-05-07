package db.sjtu.sk;

import java.text.SimpleDateFormat;
import java.util.*;

import url.manager.sjtu.sk.URL;
import util.sjtu.sk.Triple;

public class DBReader {
	public static List<Object> readDataFromDB(Date date, String col_name, String dto, List<List<Triple>> filter, List<String> keys) {
		DBController dc = DBController.createDBController("localhost", 27017);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		col_name = "data" + dateFormat.format(date) + col_name;
		
		if((keys == null || keys.size() == 0) && 
				(filter == null || filter.size() == 0)) {
			return dc.queryAll(col_name, dto);
		}
		return dc.queryByWhere(col_name, dto, filter, keys);
		
	}
	
	public static List<URL> readVisitedURLFromDB(Date date) {
		List<URL> res = new ArrayList<URL>();
		
		DBController dc = DBController.createDBController("localhost", 27017);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String col_name = "visitedURL" + dateFormat.format(date);
		
		List<Object> list = dc.queryAll(col_name, "url.manager.sjtu.sk.URL");
		for(Object obj : list) 
			res.add((URL)obj);
		
		return res;
	}
	
	public static void main(String args[]) {
		//DBReader.readDataFromDB(new Date(), "leetcodeProblemTitles", "dto.user.LeetCodeTitleDTO", null, null);
		DBReader.readVisitedURLFromDB(new Date());
	}
}

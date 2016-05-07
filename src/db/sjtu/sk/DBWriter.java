package db.sjtu.sk;

import java.text.SimpleDateFormat;
import java.util.*;

public class DBWriter {
	
	public static void writeData2DB(List<Object> data, String col_name, String dto) {
		DBController dc = DBController.createDBController("localhost", 27017);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date now = new Date();	
		col_name = "data" + dateFormat.format(now) + col_name;
		dc.createCol(col_name);
		
		dc.insert(col_name, data, dto);
	}
	

}

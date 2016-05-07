package outputer.sjtu.sk;

import java.util.Date;
import java.util.List;

import db.sjtu.sk.DBController;

public class DBOutputer extends Outputer{
	
	public boolean output(String path, List<Object> data) {
		DBController dbc = DBController.createDBController("localhost", 27017);
		// date before insert
		Date now = new Date(); 
		dbc.createCol("textData");
		
		dbc.insert("textData", data, "dto.user")
		return true;
	}
	
	public static void main(String[] args) {
		

	}

}

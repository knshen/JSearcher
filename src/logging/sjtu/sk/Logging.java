package logging.sjtu.sk;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logging {
	public static void log(String str) {
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
	
		System.out.println(dateFormat.format(now) + "\n" + str); 	
	}
	
	public static void main(String args[]) {
		Logging.log("123");
	}
}

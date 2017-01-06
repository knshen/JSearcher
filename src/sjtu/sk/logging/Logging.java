package sjtu.sk.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;

/**
 * Log info to files
 * @author ShenKai
 *
 */
public class Logging {
	private static BufferedWriter bw = null;
	static {
		try {
			createLogDir();
			bw = new BufferedWriter(new FileWriter(new File("logs//console.log")));
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void log(String str) {
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//try {
			//bw.write(dateFormat.format(now) + "\n" + str + "\n");
			//bw.flush();
		//} catch(IOException ioe) {
		//	ioe.printStackTrace();
		//}
		
		System.out.println(dateFormat.format(now) + "\n" + str); 	
	}
	
	private static void createLogDir() {
		File log_dir = new File("logs");
		if(!log_dir.exists())
			log_dir.mkdir();
	}
	
	public static void main(String args[]) {
		Logging.log("123");
	}
}

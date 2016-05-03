package logging.sjtu.sk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;

public class Logging {
	private static BufferedWriter bw = null;
	static {
		try {
			bw = new BufferedWriter(new FileWriter(new File("logs//console.log")));
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static void log(String str) {
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			bw.write(dateFormat.format(now) + "\n" + str + "\n");
			bw.flush();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		System.out.println(dateFormat.format(now) + "\n" + str); 	
	}
	
	
	public static void main(String args[]) {
		Logging.log("123");
	}
}

package test.pojCrawler;

import java.util.*;
import java.io.*;
import sjtu.sk.outputer.Outputer;
import sjtu.sk.util.Util;

public class POJOutputer extends Outputer {
	public POJOutputer() {
		super("f://1.txt");
	}
	
	public boolean output(String task_name, List<Object> data, String dto) {
		File file = new File(this.path);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file)); 
			for() {
				
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return true;
	}
}

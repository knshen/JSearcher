package util.sjtu.sk;

import java.util.Properties;

public class Util {
	
	public static int getCurrentOS() {
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		if(os.startsWith("win") || os.startsWith("Win"))
			return OperatingSystem.WINDOWS;
		else if(os.startsWith("lin") || os.startsWith("Lin"))
			return OperatingSystem.LINUX;
		return OperatingSystem.OTHERS;
	}
	
	public static void main(String[] args) {
		System.out.println(Util.getCurrentOS());

	}

}

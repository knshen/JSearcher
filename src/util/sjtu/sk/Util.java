package util.sjtu.sk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	
	
	public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
            
        } catch(NoSuchAlgorithmException nsae) {
        	nsae.printStackTrace();
        	return null;
        }
	}
	
	public static String encrypt(String inStr){  		  
        char[] a = inStr.toCharArray();  
        for (int i = 0; i < a.length; i++){  
            a[i] = (char) (a[i] ^ 't');  
        }  
        String s = new String(a);  
        return s;  
    }  
	
	public static String decrypt(String str) {
		return encrypt(encrypt(str));
	}
	
	public static void main(String[] args) {
		//System.out.println(Util.getCurrentOS());
		System.out.println(Util.MD5("http://www.baidu.com/"));
		System.out.println(Util.encrypt("http://www.baidu.com/"));
		System.out.println(Util.decrypt("http://www.baidu.com/"));
		//System.out.println(Util.MD5("http://www.sina.com/"));
		//System.out.println(Util.MD5("http://www.ifeng.com/"));
	}

}

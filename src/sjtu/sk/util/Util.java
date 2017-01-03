package sjtu.sk.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Util {
	/**
	 * Use JSoup to download binary file according to URL
	 * @param urlPath : resource URL
	 * @param savePath : local save path (on disk)
	 * @return : if succeed?
	 */
	public static boolean downloadBinary(String urlPath, String savePath) {
		if(!Util.isURLLegal(urlPath))
			return false;
		
		urlPath = urlPath.trim();
		FileOutputStream outputStream = null;
		try {
			Connection conn = Jsoup.connect(urlPath).ignoreContentType(true);		 
			Response response = conn.execute();
			if(response.statusCode() != 404) {
				byte data[] = response.bodyAsBytes();
				outputStream = new FileOutputStream(savePath);
	            outputStream.write(data);
	            
	            return true;
			}	
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			try {
				if(outputStream != null) 
					outputStream.close();
								
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
		}
		return false;
	}

	public static int getRandomInteger(int from, int to) {
		return (int)(Math.random() * (to - from)) + from;
	}
	
	public static int increaseOne(int num, int val) {
		if(num >= val) 
			return num;
		return ++num;
	}
	
	public static String getLocalIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch(UnknownHostException uhe) {
			uhe.printStackTrace();
			return null;
		}
	}
	
	public static boolean isURLLegal(String url) {
		if(url == null)
			return false;
		url = url.trim();
		
		if(url.equals(""))
			return false;
		
		if(!(url.startsWith("http://") || url.startsWith("https://"))) 
			return false;
		
		return true;
	}
	
	public static int getCurrentOS() {
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		if(os.startsWith("win") || os.startsWith("Win"))
			return OperatingSystem.WINDOWS;
		else if(os.startsWith("lin") || os.startsWith("Lin"))
			return OperatingSystem.LINUX;
		return OperatingSystem.OTHERS;
	}
	
	/**
	 * transform a string to MD5 code
	 * @param s : string
	 * @return MD5 code
	 */
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
	
	/**
	 * transform a user defined object to a DBObject
	 * @param pojo
	 * @param dto
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static DBObject serialize(Object pojo, String dto) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
		DBObject new_doc = new BasicDBObject();
		Class<?> class_name = Class.forName(dto);
		
		Field[] fields = class_name.getDeclaredFields();
		Method[] methods = class_name.getDeclaredMethods();
		
		for(Field f : fields) {
			String fn = f.getName().toLowerCase();
			for(Method m : methods) {
				String mn = m.getName().toLowerCase();
				if(mn.startsWith("get") && mn.indexOf(fn) != -1) {
					new_doc.put(f.getName(), m.invoke(pojo, null));				
					break;
				}
			}
			
		}
		return new_doc;
	}
	
	/**
	 * transform a user defined object to a Map
	 * @param dto
	 * @param pojo
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static Map<String, Object> serialize(String dto, Object pojo) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
		Map<String, Object> map = new HashMap<String, Object>();
		Class<?> class_name = Class.forName(dto);
		
		Field[] fields = class_name.getDeclaredFields();
		Method[] methods = class_name.getDeclaredMethods();

		for(Field f : fields) {
			String fn = f.getName().toLowerCase();
			for(Method m : methods) {
				String mn = m.getName().toLowerCase();
				if(mn.startsWith("get") && mn.indexOf(fn) != -1) {
					map.put(f.getName(), m.invoke(pojo, null));
					break;
				}
			}
		}
		return map;
	}
	
	/**
	 * the reverse process of serialize
	 * @param obj
	 * @param dto
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object deserialize(DBObject obj, String dto) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class<?> class_name = Class.forName(dto);
		Object pojo = class_name.newInstance();
		Map<String, Object> map = obj.toMap();
		
		Method[] methods = class_name.getDeclaredMethods();
		Field[] fields = class_name.getDeclaredFields();
		
		for(Field f : fields) {
			String fn = f.getName().toLowerCase();
			for(Method m : methods) {
				String mn = m.getName().toLowerCase();
				if(mn.startsWith("set") && mn.indexOf(fn) != -1) {
					m.invoke(pojo, map.get(f.getName()));
				}
			}
		}
		
		return pojo;
	}
	
	/**
	 * deserialize: from a map to a pojo object
	 * @param map
	 * @param dto
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object deserialize(Map<String, Object> map, String dto) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class<?> class_name = Class.forName(dto);
		Object pojo = class_name.newInstance();
		
		Method[] methods = class_name.getDeclaredMethods();
		Field[] fields = class_name.getDeclaredFields();
		
		for(Field f : fields) {
			String fn = f.getName().toLowerCase();
			for(Method m : methods) {
				String mn = m.getName().toLowerCase();
				if(mn.startsWith("set") && mn.indexOf(fn) != -1) {
					m.invoke(pojo, map.get(f.getName()));
				}
			}
		}

		return pojo;
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

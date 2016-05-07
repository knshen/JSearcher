package util.sjtu.sk;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

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
	
	public static void main(String[] args) {
		//System.out.println(Util.getCurrentOS());
		System.out.println(Util.MD5("http://www.baidu.com/"));
		System.out.println(Util.encrypt("http://www.baidu.com/"));
		System.out.println(Util.decrypt("http://www.baidu.com/"));
		//System.out.println(Util.MD5("http://www.sina.com/"));
		//System.out.println(Util.MD5("http://www.ifeng.com/"));
	}

}

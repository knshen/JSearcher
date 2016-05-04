package demo;

import java.util.*;
import com.mongodb.*;
//import com.mongodb.client.MongoDatabase;

public class DBDemo {
	public static void old_test() throws Exception {
		Mongo mg = new Mongo("localhost" , 27017);
		DB db = mg.getDB("test");
		DBCollection users = db.getCollection("restaurants");
        
        //查询所有的数据
        DBCursor cur = users.find();
        int c = 0;
        while (c < 2 && cur.hasNext()) {
        	DBObject doc = cur.next();
        	System.out.println(doc);
        	Set<String> keys = doc.keySet();
            c++;
        }
        
        System.out.println("before: " + users.count());
        //插入数据
        List<DBObject> list = new ArrayList<DBObject>();
        DBObject new_doc = new BasicDBObject();
        new_doc.put("name", "hoojo");
        new_doc.put("age", 24);
        new_doc.put("sex", "male");
        
        list.add(new_doc);
        users.insert(list).getN();
        System.out.println("after: " + users.count());
	}
	
	public static void main(String[] args) throws Exception {
		DBDemo.old_test();
	}

}

package db.sjtu.sk;

import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.*;
import com.mongodb.*;

import dto.user.Person;
import util.sjtu.sk.Condition;
import util.sjtu.sk.Triple;
import util.sjtu.sk.Util;

public class DBController {
	private Mongo mg = null;
	private DB db = null;
	private volatile static DBController dc = null;
	
	private DBController(String ip, int port) {
		try {
			mg = new Mongo(ip, port);
			// connect to default database
			db = mg.getDB("jsearcher");
		} catch(UnknownHostException uhe) {
			uhe.printStackTrace();
		}
	}
	
	public static DBController createDBController(String ip, int port) {
		if(dc == null) {
			synchronized(DBController.class) {
				if(dc == null) {
					dc = new DBController(ip, port);
				}
			}
		}
		return dc;
	}
	
	/**
	 * create a collection if it doesn't exist
	 * @param col_name : colletion name
	 */
	public void createCol(String col_name) {
		if(db.getCollection(col_name) == null)
			db.createCollection(col_name, null);
	}
	
	/**
	 * insert a list of data to a collection
	 * @param col_name : colletion name
	 * @param data : data to insert
	 * @param dto : data type(complete path)
	 * @return
	 */
	public boolean insert(String col_name, List<Object> data, String dto) {
		if(mg == null)
			return false;
		
		List<DBObject> list = new ArrayList<DBObject>();
		try {
			for(Object pojo : data) 
				list.add(Util.serialize(pojo, dto));
		} catch(IllegalAccessException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch(InvocationTargetException ite) {
			ite.printStackTrace();
		}
		
			
		DBCollection col = db.getCollection(col_name);
		col.insert(list);
		return true;
	}
	
	/**
	 * query all the data in a collection
	 * @param col_name : colletion name
	 * @param dto : data type
	 * @return data
	 */
	public List<Object> queryAll(String col_name, String dto) {
		DBCollection dbc = db.getCollection(col_name);
        DBCursor cur = dbc.find();
        List<Object> res = new ArrayList<Object>();
        try {
        	while (cur.hasNext()) {
        		DBObject doc = cur.next();
        		try {
        			res.add(Util.deserialize(doc, dto));
        		} catch(InvocationTargetException ite) {
        			ite.printStackTrace();
        		}     		
        		//System.out.println(doc);
            }
        } catch(ClassNotFoundException cnfe) {
        	cnfe.printStackTrace();
        } catch(InstantiationException ie) {
        	ie.printStackTrace();
        } catch(IllegalAccessException iae) {
        	iae.printStackTrace();
        }
        return res;
	}
	
	
	public List<Object> queryByWhere(String col_name, String dto, List<List<Triple>> filter, List<String> keys) {
		List<Object> res = new ArrayList<Object>();
		DBCollection dbc = db.getCollection(col_name);
		// filter
		BasicDBObject ref = new BasicDBObject();
		DBObject _keys = new BasicDBObject();
		
		for(String key : keys) 
			_keys.put(key, true);
		
		for(List<Triple> and : filter) {
			for(Triple tri : and) {
				if(!ref.containsField(tri.key_name)) {
					if(tri.condition.equals(Condition.EQUAL)) 
						ref.put(tri.key_name, tri.value);
					
					else 
						ref.put(tri.key_name, new BasicDBObject(tri.condition, tri.value));
				}
				else {
					if(tri.condition.equals(Condition.EQUAL)) {
						ref.put(tri.key_name, arg1)
					}
					else {
						
					}
				}
			}
		}
		
		try {
	        DBCursor cur = dbc.find(ref, _keys);
	    	while (cur.hasNext()) {
	    		DBObject doc = cur.next();
	    		try {
	    			res.add(Util.deserialize(doc, dto));
	    		} catch(InvocationTargetException ite) {
	    			ite.printStackTrace();
	    		}     		
	    		//System.out.println(doc);
	        }

		} catch(ClassNotFoundException cnfe) {
        	cnfe.printStackTrace();
        } catch(InstantiationException ie) {
        	ie.printStackTrace();
        } catch(IllegalAccessException iae) {
        	iae.printStackTrace();
        }

    	return res;
	} 
	
	
	public static void main(String[] args) throws Exception {
		// unit test
		DBController dc = DBController.createDBController("localhost", 27017);
		
		dc.createCol("demo");
		List<Object> data = new ArrayList<Object>();
		for(int i=25; i<=30; i++) {
			Person p = new Person(i, "male");
			data.add(p);
		}
		
		//dc.insert("demo", data, "dto.user.Person");
		//List<Object> dtos = dc.queryAll("demo", "dto.user.Person");
		
		
		List<List<Triple>> filter = new ArrayList<List<Triple>>();
		List<Triple> list = new ArrayList<Triple>();
		list.add(new Triple("age", Condition.MORE_EQUAL, 26));
		//list.add(new Triple("sex", Condition.EQUAL, "male"));
		list.add(new Triple("age", Condition.LESS_EQUAL, 28));
		filter.add(list);
		
		List<Object> dtos = dc.queryByWhere("demo", "dto.user.Person", filter, Arrays.asList("age"));
		
		for(Object dto : dtos) {
			System.out.println(dto);
		}
	}

}

package sjtu.sk.storage;

import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.*;

import com.mongodb.*;

import sjtu.sk.util.Condition;
import sjtu.sk.util.Triple;
import sjtu.sk.util.Util;
import test.dto.Person;

/**
 * Java API that encapsulates part of MongoDB query and insert operations
 * @author ShenKai
 *
 */
public class MongoDBController {
	private Mongo mg = null;
	private DB db = null;
	private volatile static MongoDBController dc = null;
	
	private MongoDBController(String ip, int port, String db_name) {
		try {
			mg = new Mongo(ip, port);
			// connect to default database
			db = mg.getDB(db_name);
		} catch(UnknownHostException uhe) {
			uhe.printStackTrace();
		}
	}
	
	public static MongoDBController createDBController(String ip, int port, String db_name) {
		if(dc == null) {
			synchronized(MongoDBController.class) {
				if(dc == null) {
					dc = new MongoDBController(ip, port, db_name);
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
	
	public boolean removeAll(String col_name) {
		if(mg == null)
			return false;
		DBCollection dbc = db.getCollection(col_name);
		dbc.drop();
		return true;
	}
	
	/**
	 * insert a list of data to a collection. Note that the data is a list of
	 * objects defined by user; User must pass the complete class name of his
	 * own class(like java.util.HashMap). By default, the objects within the
	 * list must be the same type.
	 * 
	 * @param col_name : colletion name
	 * @param data : data to insert
	 * @param dto : data type(complete path)
	 * @return if the insert is successful
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
	 * @param dto : complete data type defined by user
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
	
	/**
	 * query data in one collection with a filter(and or condition) 
	 * 
	 * @param col_name : collection name
	 * @param dto : complete data type defined by user
	 * @param filter : combined "and","or" conditions, the rule is as follows:
	 * [ [triple and triple ... and triple] or ... or [triple and triple ... and triple] ],
	 * a triple is defined as (key_name, condition, value)
	 * 
	 * @param keys : fields to query
	 * @return data
	 */
	public List<Object> queryByWhere(String col_name, String dto, List<List<Triple>> filter, List<String> keys) {
		List<Object> res = new ArrayList<Object>();
		DBCollection dbc = db.getCollection(col_name);
		// filter
		BasicDBObject all_ref = new BasicDBObject();
		DBObject _keys = new BasicDBObject();
		
		if(keys != null)
			for(String key : keys) 
				_keys.put(key, true);
		
		BasicDBList list = new BasicDBList();
		for(List<Triple> and : filter) {
			BasicDBObject ref = new BasicDBObject();
			for(Triple tri : and) {
				if(!ref.containsField(tri.key_name)) {
					if(tri.condition.equals(Condition.EQUAL)) 
						ref.put(tri.key_name, tri.value);
					
					else 
						ref.put(tri.key_name, new BasicDBObject(tri.condition, tri.value));
									
				}
				else {
					if(tri.condition.equals(Condition.EQUAL)) {
						// TODO It is impossible
					}
					else {
						BasicDBObject bo = (BasicDBObject)ref.get(tri.key_name);
						bo.append(tri.condition, tri.value);						
					}
				}
			} // end an "and" query
			list.add(ref);
		}
		
		all_ref.put("$or", list);
		
		try {
	        DBCursor cur = dbc.find(all_ref, _keys);
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
		MongoDBController dc = MongoDBController.createDBController("localhost", 27017, "jsearcher");
			
		dc.createCol("demo");
		List<Object> data = new ArrayList<Object>();
		for(int i=25; i<=30; i++) {
			Person p = new Person(i, "male");
			data.add(p);
		}
		
		//dc.insert("demo", data, "dto.user.Person");
		//List<Object> dtos = dc.queryAll("demo", "dto.user.Person");
		
		
		List<List<Triple>> filter = new ArrayList<List<Triple>>();
		List<Triple> list1 = new ArrayList<Triple>();
		list1.add(new Triple("age", Condition.MORE_EQUAL, 26));
		list1.add(new Triple("age", Condition.LESS_EQUAL, 27));
		List<Triple> list2 = new ArrayList<Triple>();
		list2.add(new Triple("age", Condition.MORE_EQUAL, 29));
		list2.add(new Triple("age", Condition.LESS_EQUAL, 30));
		List<Triple> list3 = new ArrayList<Triple>();
		list3.add(new Triple("age", Condition.EQUAL, 25));
		
		filter.add(list1);
		filter.add(list2);
		filter.add(list3);
		List<Object> dtos = dc.queryByWhere("demo", "dto.user.Person", filter, null);
		
		for(Object dto : dtos) {
			System.out.println(dto);
		}
		
	}

}

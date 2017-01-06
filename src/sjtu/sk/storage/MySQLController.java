package sjtu.sk.storage;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.Date;

import sjtu.sk.scheduler.SpiderConfig;
import sjtu.sk.util.Util;

import org.apache.activemq.spring.Utils;

public class MySQLController {
	private Connection conn = null;
	private Statement stmt = null;

	private static MySQLController mc = null;

	public static MySQLController createDBController(String host, int port,
			String user, String pass, String db_name) {
		if (mc == null) {
			synchronized (MySQLController.class) {
				if (mc == null)
					mc = new MySQLController(host, port, user, pass, db_name);
			}
		}
		return mc;
	}

	private MySQLController(String host, int port, String user, String pass, String db_name) {
		try {
			String url = "jdbc:mysql://" + host + ":" + port + "/" + db_name + "?"
					+ "user=" + user + "&password=" + pass
					+ "&useUnicode=true&characterEncoding=UTF8";
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url);
			stmt = conn.createStatement();
			 
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (ClassNotFoundException ce) {
			ce.printStackTrace();
		}
	}

	/**
	 * create a table
	 * @param table : table name
	 * @param cols : table columns, key:column name; value:column data type
	 * A column named "id" will be created as primary key by default
	 * But we suggest that you would better create tables with sql by yourself rather than
	 * using this API 
	 */
	public void createTable(String table, Map<String, String> cols) {
		String sql = "create table " + table + "\n" +
				"(" + "\n" +
				"id int primary key auto_increment, \n";
		
		for(Map.Entry<String, String> entry : cols.entrySet()) {
			String name = entry.getKey();
			String type = entry.getValue();
			sql += name + " " + type + " not null, \n";
		}
		
		sql = sql.substring(0, sql.length()-3);
		sql += "\n)";
		//System.out.println(sql);
		try {
			stmt.executeUpdate(sql);
		} catch(SQLException se) {
			se.printStackTrace();
		}
	}
	
	public List<Object> query(String dto, String sql, List<String> col_names, List<String> col_types) {
		List<Object> res = new ArrayList<Object>();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				Map<String, Object> map = formDataAsMap(rs, col_names, col_types);
				res.add(Util.deserialize(map, dto));
			}
		} catch(SQLException se) {
			se.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (InstantiationException ie) {
			ie.printStackTrace();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
		}
		return res;
	}
	
	/**
	 * select * from table
	 * @param table
	 * @param dto : the dto name with respect to the table 
	 * @param col_names : column names
	 * @param col_types : data type of each column
	 * @return
	 */
	public List<Object> queryAll(String table, String dto, List<String> col_names, List<String> col_types) {
		List<Object> res = new ArrayList<Object>();
		try {
			ResultSet rs = stmt.executeQuery("select * from " + table); 
			while(rs.next()) {
				// each record
				Map<String, Object> map = formDataAsMap(rs, col_names, col_types);
				try {
					res.add(Util.deserialize(map, dto));
				} catch (ClassNotFoundException cnfe) {
					cnfe.printStackTrace();
				} catch (InstantiationException ie) {
					ie.printStackTrace();
				} catch (IllegalAccessException iae) {
					iae.printStackTrace();
				} catch (InvocationTargetException ite) {
					ite.printStackTrace();
				}
			}
			
		} catch(SQLException se) {
			se.printStackTrace();
		}
		
		return res;
	}
	
	private Map<String, Object> formDataAsMap(ResultSet rs, List<String> col_names, List<String> col_types) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			for(int i=0; i<col_types.size(); i++) {
				if(col_types.get(i).equals("String"))
					map.put(col_names.get(i), rs.getString(i+1));
				else if(col_types.get(i).equals("int"))
					map.put(col_names.get(i), rs.getInt(i+1));
				else if(col_types.get(i).equals("long"))
					map.put(col_names.get(i), rs.getLong(i+1));
				else if(col_types.get(i).equals("double") || col_types.get(i).equals("float"))
					map.put(col_names.get(i), rs.getDouble(i+1));
				else if(col_types.get(i).equals("Date"))
					map.put(col_names.get(i), rs.getDate(i+1));
			}

		} catch(SQLException se) {
			se.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 
	 * @param table
	 * @param data
	 * @param dto
	 * @param col_names : column names
	 * @param col_types : column data types with respect to col_names
	 * @return
	 */
	public boolean insert(String table, List<Object> data, String dto, List<String> col_names, List<String> col_types) {
		String sql = "insert into " + table + " (";
		for(int i=0; i<col_names.size(); i++) 
			if(i == col_names.size()-1)
				sql += col_names.get(i) + ") values(";
			else
				sql += col_names.get(i) + ", ";
		
		for(int i=0; i<col_names.size(); i++) 
			if(i == col_names.size()-1)
				sql += "?)";
			else
				sql += "?, ";
			
		try {
			for(Object pojo : data) {
				Map<String, Object> map = Util.serialize(dto, pojo);
				PreparedStatement preStmt = conn.prepareStatement(sql);
				for(int i=0; i<col_names.size(); i++) {
					if(col_types.get(i).equals("int"))
						preStmt.setInt(i+1, Integer.parseInt(map.get(col_names.get(i)).toString()));
					else if(col_types.get(i).equals("long"))
						preStmt.setLong(i+1, Long.parseLong(map.get(col_names.get(i)).toString()));
					else if(col_types.get(i).equals("float") || col_types.get(i).equals("double"))
						preStmt.setDouble(i+1, Double.parseDouble(map.get(col_names.get(i)).toString()));
					else if(col_types.get(i).equals("String")) 
						preStmt.setString(i+1, map.get(col_names.get(i)).toString());
					else if(col_types.get(i).equals("Date"))
						preStmt.setString(i+1, map.get(col_names.get(i)).toString());
				}
				
				preStmt.executeUpdate(); 
			}
			return true;
		} catch (IllegalAccessException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (InvocationTargetException ite) {
			ite.printStackTrace();
		} catch(SQLException se) {
			se.printStackTrace();
		}
		return false;
	}

	public boolean removeAll(String table) {
		String sql = "delete from " + table;
		try {
			stmt.executeUpdate(sql);
			return true;
		} catch(SQLException se) {
			se.printStackTrace();
		}
		return false;
	}
	
	public void close() {
		try {
			this.conn.close();
			this.stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		MySQLController mc = MySQLController.createDBController("localhost", 3306, "root", "root", "JSearcher");
		//mc.queryAll("test", "sjtu.sk.storage.TestDTO", Arrays.asList("id", "name", "age"), Arrays.asList("int", "String", "int"));
		//mc.insert("test", Arrays.asList(dto), "sjtu.sk.storage.TestDTO", Arrays.asList("id", "name", "age"), Arrays.asList("int", "String", "int"));
		//mc.query("sjtu.sk.storage.TestDTO", "select * from test where age < 24", Arrays.asList("id", "name", "age"), Arrays.asList("int", "String", "int"));
		//mc.queryAll("poj", "test.dto.OJDTO", Arrays.asList("id", "title", "content", "submit", "accept", "acRatio", "url"), Arrays.asList("int", "String", "String", "long", "long", "double", "String"));
	}

}

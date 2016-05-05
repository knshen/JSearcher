package util.sjtu.sk;
/**
 * this class is written for mongodb query by where 
 * @author knshen
 *
 */
public class Triple {
	public String key_name;
	public String condition;
	public Object value;
	
	public Triple(String key_name, String cond, Object v) {
		this.key_name = key_name;
		this.condition = cond;
		this.value = v;
	}
}

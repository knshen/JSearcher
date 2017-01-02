package sjtu.sk.outputer;

import java.io.*;
import java.util.*;

/**
 * Outputer is used to persist crawled data
 * @author ShenKai
 *
 */
public abstract class Outputer {
	protected String path;
	public Outputer(String path) {
		this.path = path;
	}
	
	public abstract boolean output(String task_name, List<Object> data, String dto);
		
}

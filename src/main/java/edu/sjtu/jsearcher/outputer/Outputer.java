package edu.sjtu.jsearcher.outputer;

import java.util.*;

/**
 * Outputer is used to persist crawled data
 * @author ShenKai
 *
 */
public abstract class Outputer {
	protected String path;
	
	public Outputer() {
		
	}
	
	public Outputer(String path) {
		this.path = path;
	}
	
	public abstract boolean output(String task_name, List<Object> data);
		
}

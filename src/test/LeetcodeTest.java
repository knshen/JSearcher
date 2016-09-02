package test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import sjtu.sk.filter.LeetcodeURLFilter;
import sjtu.sk.parser.LeetcodeProblemExtractor;
import sjtu.sk.scheduler.DefaultScheduler;
import sjtu.sk.url.manager.LeetcodeURLComparator;
import sjtu.sk.url.manager.URL;
import sjtu.sk.util.PersistentStyle;

public class LeetcodeTest {
	/**
	 * this is the entrance of application
	 * @param args
	 */
	public static void main(String[] args) {		
		//demo 1: crawl leetcode problem title
		URL seed = new URL("https://leetcode.com/problemset/algorithms/");
		//create scheduler instance
		DefaultScheduler ds = DefaultScheduler.createDefaultScheduler();
		// config parameters
		/**
		 * parameters:
		 * (1) dataextractor
		 * (2) num_threads
		 * (3) isthreadpool
		 * (4) maxnum
		 * (5) persistent_style
		 * (6) task_name
		 * (7) dto
		 * (8) filter(*)
		 * (9) outputer(*)
		 * (10)comparator(*)
 		 */
		Map<String, Object> paras = new HashMap<String, Object>();
		
		paras.put("dataExtractor", new LeetcodeProblemExtractor());
		paras.put("num_threads", 20);
		paras.put("isThreadPool", false);
		paras.put("maxNum", 500);
		paras.put("persistent_style", PersistentStyle.ES);
		paras.put("task_name", "oj-leetcode");
		paras.put("dto", "dto.user.LeetcodeProblemDTO");
		paras.put("comparator", new LeetcodeURLComparator());
		//paras.put("filter", new LeetcodeURLFilter());
		//paras.put("outputer", null);
		ds.config(paras);
		
		// run tasks
		ds.runTask(Arrays.asList(seed));
	}			

}

package test;

import java.util.*;

import sjtu.sk.parser.LeetcodeProblemExtractor;
import sjtu.sk.parser.POJProblemExtractor;
import sjtu.sk.scheduler.DefaultScheduler;
import sjtu.sk.url.manager.LeetcodeURLComparator;
import sjtu.sk.url.manager.URL;
import sjtu.sk.util.PersistentStyle;

public class POJTest {

	public static void main(String[] args) {
		//demo 2: crawl poj problem
		List<URL> seeds = new ArrayList<URL>();
		for(int i=1000; i<=4054; i++) {
			URL seed = new URL("http://poj.org/problem?id=" + i);
			seeds.add(seed);
		}
		
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
		
		paras.put("dataExtractor", new POJProblemExtractor());
		paras.put("num_threads", 20);
		paras.put("isThreadPool", false);
		paras.put("maxNum", 3000);
		paras.put("persistent_style", PersistentStyle.ES);
		paras.put("task_name", "oj-poj");
		paras.put("dto", "dto.user.POJDTO");
		//paras.put("comparator", null);
		//paras.put("filter", new LeetcodeURLFilter());
		//paras.put("outputer", null);
		ds.config(paras);
		
		// run tasks
		ds.runTask(seeds);

	}

}

package test.hduCrawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sjtu.sk.scheduler.DefaultScheduler;
import sjtu.sk.url.manager.URL;
import sjtu.sk.util.PersistentStyle;

public class HDUTest {

	public static void main(String[] args) {
		//demo: crawl hdu oj problems
		List<URL> seeds = new ArrayList<URL>();
		for(int i=1000; i<=5500; i++) {
			URL seed = new URL("http://acm.hdu.edu.cn/showproblem.php?pid=" + i);
			seeds.add(seed);
		}
		
		System.out.println("finish adding seeds!");
		
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
		
		paras.put("dataExtractor", new HDUProblemExtractor());
		paras.put("num_threads", 20);
		paras.put("isThreadPool", false);
		paras.put("maxNum", 5000);
		paras.put("persistent_style", PersistentStyle.ES);
		paras.put("task_name", "oj-hdu");
		paras.put("dto", "dto.user.OJDTO");
		//paras.put("comparator", null);
		paras.put("filter", new HDULinkFilter());
		//paras.put("outputer", null);
		ds.config(paras);
		
		// run tasks
		ds.runTask(seeds);


	}

}

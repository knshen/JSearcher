package test.bbsCrawler;

import java.util.*;

import sjtu.sk.scheduler.DefaultScheduler;
import sjtu.sk.url.manager.URL;
import sjtu.sk.util.PersistentStyle;
import test.pojCrawler.BBSPostLinkFilter;

public class BBSTest {

	public static void main(String[] args) {
		//demo 2: crawl bbs jobInfo posts
		
		final int first_id = 6775; // 6775
		
		List<URL> seeds = new ArrayList<URL>();
		for(int i=first_id; i>=6400; i--) {
			URL url = new URL("https://bbs.sjtu.edu.cn/bbsdoc,board,JobInfo,page,"
					+ i + ".html");
			seeds.add(url);
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
		
		paras.put("dataExtractor", new BBSPostExtractor());
		paras.put("num_threads", 1);
		paras.put("maxNum", 100);
		paras.put("persistent_style", PersistentStyle.ES);
		paras.put("task_name", "bbs-test");
		paras.put("dto", "dto.user.BBSPostDTO");
		//paras.put("comparator", null);
		paras.put("filter", new BBSPostLinkFilter());
		//paras.put("outputer", null);
		ds.config(paras);
		
		// run tasks
		ds.runTask(seeds);

	}

}

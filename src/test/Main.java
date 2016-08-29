package test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import sjtu.sk.parser.LeetcodeProblemExtractor;
import sjtu.sk.scheduler.DefaultScheduler;
import sjtu.sk.url.manager.URL;
import sjtu.sk.util.PersistentStyle;

public class Main {
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
		Map<String, Object> paras = new HashMap<String, Object>();
		//paras.put("OutPuter", new HtmlTableOutputer());
		paras.put("dataExtractor", new LeetcodeProblemExtractor());
		paras.put("num_threads", 1);
		paras.put("isThreadPool", false);
		paras.put("maxNum", 30);
		paras.put("persistent_style", PersistentStyle.ES);
		paras.put("task_name", "leetcode-problem");
		paras.put("dto", "dto.user.LeetCodeProblemDTO");
		ds.config(paras);
		
		// run tasks
		ds.runTask(Arrays.asList(seed));
		
		/*
		//demo 2: crawl images
		URL seed = new URL("http://ent.qq.com/star/");
		DefaultScheduler ds = DefaultScheduler.createDefaultScheduler();
		ds.config(null, new ImageExtractor(), 3, false, 10);
		ds.runTask(Arrays.asList(seed), "qqStarPic", "dto.user.Picture");
		*/
	}			

}

package sjtu.sk.scheduler;

import java.util.*;

import sjtu.sk.logging.Logging;
import sjtu.sk.parser.LeetcodeProblemExtractor;
import sjtu.sk.url.manager.URL;
import sjtu.sk.util.PersistentStyle;

public class MultiTaskSecheduler implements Runnable {
	private List<DefaultScheduler> crawlers = null;
	private int num_tasks = 1;
	private List<Thread> tasks = null;
	private List<List<URL>> seeds = new ArrayList<List<URL>>();
	
	private MultiTaskSecheduler(int num_tasks) {
		this.num_tasks = num_tasks;
		this.crawlers = new ArrayList<DefaultScheduler>();
		for(int i=0; i<num_tasks; i++)
			crawlers.add(DefaultScheduler.createDefaultScheduler());
		
		tasks = new ArrayList<Thread>();
		for(int i=0; i<num_tasks; i++)
			tasks.add(new Thread(this));
	}
	
	public static MultiTaskSecheduler createMultiTaskSecheduler(int num_tasks) {
		return new MultiTaskSecheduler(num_tasks);
	}
	
	public final void config(List<Map<String, Object>> paras, List<List<URL>> seeds) {
		if(paras == null || paras.size() != num_tasks) {
			Logging.log("configure fail!\n");
			return;
		}		
		if(seeds == null || seeds.size() != num_tasks) {
			Logging.log("configure fail!\n");
			return;
		}
		
		for(int i=0; i<num_tasks; i++) 
			crawlers.get(i).config(paras.get(i));
		
		this.seeds = seeds;
	}
	
	public void runTasks() {
		for(Thread th : tasks) {
			th.start();
		}
		
		for(Thread th : tasks) {
			try {
				th.join();
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
	
	public void run() {
		int task_id = Integer.parseInt(Thread.currentThread().getName().split("-")[1]);
		// runTask
		crawlers.get(task_id).runTask(seeds.get(task_id));
	}
	
	public static void main(String[] args) {
		MultiTaskSecheduler mts = MultiTaskSecheduler.createMultiTaskSecheduler(2);

		URL seed = new URL("https://leetcode.com/problemset/algorithms/");
		// configure parameters
		Map<String, Object> paras1 = new HashMap<String, Object>();
		paras1.put("dataExtractor", new LeetcodeProblemExtractor());
		paras1.put("num_threads", 5);
		paras1.put("isThreadPool", false);
		paras1.put("maxNum", 30);
		paras1.put("persistent_style", PersistentStyle.ES);
		paras1.put("task_name", "leetcode-problem");
		paras1.put("dto", "dto.user.LeetCodeProblemDTO");
		
		/*
		Map<String, Object> paras2 = new HashMap<String, Object>();
		paras2.put("dataExtractor", new LeetcodeProblemTitleExtractor());
		paras2.put("num_threads", 5);
		paras2.put("isThreadPool", false);
		paras2.put("maxNum", 30);
		paras2.put("persistent_style", PersistentStyle.ES);
		paras2.put("task_name", "leetcode-problemTitle");
		paras2.put("dto", "dto.user.LeetCodeTitleDTO");
		*/
		List<Map<String, Object>> paras = new ArrayList<Map<String, Object>>();
		paras.add(paras1);
		//paras.add(paras2);
		
		List<List<URL>> seeds = new ArrayList<List<URL>>();
		seeds.add(Arrays.asList(seed));
		seeds.add(Arrays.asList(seed));
		mts.config(paras, seeds);
		mts.runTasks();
	}

}

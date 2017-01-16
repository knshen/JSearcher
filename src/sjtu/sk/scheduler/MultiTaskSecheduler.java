package sjtu.sk.scheduler;

import java.util.*;

import org.apache.log4j.Logger;
import sjtu.sk.url.manager.URL;
import sjtu.sk.util.PersistentStyle;

public class MultiTaskSecheduler implements Runnable {
	private List<DefaultScheduler> crawlers = null;
	private int num_tasks = 1;
	private List<Thread> tasks = null;
	private List<List<URL>> seeds = new ArrayList<List<URL>>();
	
	private MultiTaskSecheduler(int num_tasks, List<String> paths) {
		this.num_tasks = num_tasks;
		this.crawlers = new ArrayList<DefaultScheduler>();
		for(int i=0; i<num_tasks; i++)
			crawlers.add(DefaultScheduler.createDefaultScheduler(paths.get(i)));
		
		tasks = new ArrayList<Thread>();
		for(int i=0; i<num_tasks; i++)
			tasks.add(new Thread(this));
	}
	
	public static MultiTaskSecheduler createMultiTaskSecheduler(int num_tasks, List<String> paths) {
		return new MultiTaskSecheduler(num_tasks, paths);
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
		
	}

}

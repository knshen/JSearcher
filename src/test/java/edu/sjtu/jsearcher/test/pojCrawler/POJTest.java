package edu.sjtu.jsearcher.test.pojCrawler;

import java.util.*;

import edu.sjtu.jsearcher.scheduler.DefaultScheduler;
import edu.sjtu.jsearcher.scheduler.SpiderConfig;
import edu.sjtu.jsearcher.url.manager.URL;

public class POJTest {

	public static void main(String[] args) {
		//demo: crawl poj problem
		List<URL> seeds = new ArrayList<URL>();
		for(int i=1000; i<=2000; i++) {
			URL seed = new URL("http://poj.org/problem?id=" + i);
			seeds.add(seed);
		}
		
		//create scheduler instance and configure common parameters
		DefaultScheduler ds = DefaultScheduler.createDefaultScheduler("JSearcher.yml");
		// configure other parameters
		SpiderConfig.setDataExtractor(ds, new POJProblemExtractor());
		SpiderConfig.setOutputer(ds, new POJOutputer());
		
		// run tasks
		ds.runTask(seeds);

	}

}

package test.pojCrawler;

import java.util.*;

import sjtu.sk.scheduler.DefaultScheduler;
import sjtu.sk.scheduler.SpiderConfig;
import sjtu.sk.url.manager.URL;
import sjtu.sk.util.PersistentStyle;

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

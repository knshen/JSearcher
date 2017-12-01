package nba.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.sjtu.jsearcher.scheduler.DefaultScheduler;
import edu.sjtu.jsearcher.scheduler.SpiderConfig;
import edu.sjtu.jsearcher.test.pojCrawler.POJOutputer;
import edu.sjtu.jsearcher.test.pojCrawler.POJProblemExtractor;
import edu.sjtu.jsearcher.url.manager.URL;

public class PlayerTest {
	public static void main(String[] args) {
		//demo: crawl poj problem
		List<String> urls = Arrays.asList("http://nba.sports.sina.com.cn/team/Spurs.shtml",
			"http://nba.sports.sina.com.cn/team/Timberwolves.shtml",
			"http://nba.sports.sina.com.cn/team/Heat.shtml",
			"http://nba.sports.sina.com.cn/team/Pistons.shtml",
			"http://nba.sports.sina.com.cn/team/Celtics.shtml",
			"http://nba.sports.sina.com.cn/team/Grizzlies.shtml",
			"http://nba.sports.sina.com.cn/team/Nuggets.shtml",
			"http://nba.sports.sina.com.cn/team/Suns.shtml",
			"http://nba.sports.sina.com.cn/team/Magic.shtml",
			"http://nba.sports.sina.com.cn/team/Pacers.shtml",
			"http://nba.sports.sina.com.cn/team/76ers.shtml",
			"http://nba.sports.sina.com.cn/team/Mavericks.shtml",
			"http://nba.sports.sina.com.cn/team/Jazz.shtml",
			"http://nba.sports.sina.com.cn/team/Lakers.shtml",
			"http://nba.sports.sina.com.cn/team/Hawks.shtml",
			"http://nba.sports.sina.com.cn/team/Cavaliers.shtml",
			"http://nba.sports.sina.com.cn/team/Knicks.shtml",
			"http://nba.sports.sina.com.cn/team/Rockets.shtml",
			"http://nba.sports.sina.com.cn/team/Trail%20Blazers.shtml",
			"http://nba.sports.sina.com.cn/team/Clippers.shtml",
			"http://nba.sports.sina.com.cn/team/Wizards.shtml",
			"http://nba.sports.sina.com.cn/team/Bulls.shtml",
			"http://nba.sports.sina.com.cn/team/Nets.shtml",
			"http://nba.sports.sina.com.cn/team/Pelicans.shtml",
			"http://nba.sports.sina.com.cn/team/Thunder.shtml",
			"http://nba.sports.sina.com.cn/team/Warriors.shtml",
			"http://nba.sports.sina.com.cn/team/Hornets.shtml",
			"http://nba.sports.sina.com.cn/team/Bucks.shtml",
			"http://nba.sports.sina.com.cn/team/Raptors.shtml",
			"http://nba.sports.sina.com.cn/team/Kings.shtml");
		
		List<URL> seeds = new ArrayList<URL>();
		for(String url : urls) {
			seeds.add(new URL(url));
		}
		
		//create scheduler instance and configure common parameters
		DefaultScheduler ds = DefaultScheduler.createDefaultScheduler("JSearcher.yml");
		// configure other parameters
		SpiderConfig.setDataExtractor(ds, new PlayerExtractor());
			
		// run tasks
		ds.runTask(seeds);

	}
}

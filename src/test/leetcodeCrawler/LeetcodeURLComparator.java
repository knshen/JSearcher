package test.leetcodeCrawler;

import java.util.*;

import sjtu.sk.url.manager.URL;
import sjtu.sk.url.manager.URLComparator;

public class LeetcodeURLComparator extends URLComparator<URL> {
	public int compare(URL url1, URL url2) {
		String prefix = "https://leetcode.com/problems/";
		if(url1.getURLValue().startsWith(prefix) &&
				url2.getURLValue().startsWith(prefix))
			return 0;
		else if(url1.getURLValue().startsWith(prefix))
			return -1;
		return 1;
	}
	
}

package sjtu.sk.filter;

import java.util.*;

import sjtu.sk.url.manager.URL;

public class LeetcodeURLFilter extends URLFilter {
	public boolean shouldVisit(URL url) {
		List<String> must = new ArrayList<String>();
		List<String> must_not = new ArrayList<String>();
		must.add("problems");
		
		must_not.add("problemset");

		return isContain(url, must) && isNotContain(url, must_not);
	}
}

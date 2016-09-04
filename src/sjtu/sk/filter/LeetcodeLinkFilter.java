package sjtu.sk.filter;

import java.util.*;

import sjtu.sk.url.manager.Link;
import sjtu.sk.url.manager.URL;

public class LeetcodeLinkFilter extends LinkFilter {
	public boolean shouldVisit(Link link) {
		List<String> must = new ArrayList<String>();
		List<String> must_not = new ArrayList<String>();
		must.add("problems");
		
		must_not.add("problemset");

		return isContain(link, must) && isNotContain(link, must_not);
	}
}

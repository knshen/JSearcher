package edu.sjtu.jsearcher.filter;

import java.util.List;

import edu.sjtu.jsearcher.url.manager.Link;

public abstract class LinkFilter {
	/**
	 * check if the URL contains all the key words
	 * @param url
	 * @param paras
	 * @return
	 */
	public boolean isContain(Link link, List<String> paras) {
		for(String word : paras) {
			if(link.getLink_text().indexOf(word) == -1)
				return false;
		}
		return true;
	}
	
	/**
	 * check if the URL DOES NOT contain any key word
	 * @param url
	 * @param paras
	 * @return
	 */
	public boolean isNotContain(Link link, List<String> paras) {
		for(String word : paras) {
			if(link.getLink_text().indexOf(word) != -1)
				return false;
		}
		return true;
	}
	
	public abstract boolean shouldVisit(Link link);
}

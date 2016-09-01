package sjtu.sk.filter;

import java.util.List;

import sjtu.sk.url.manager.URL;

public abstract class URLFilter {
	/**
	 * check if the URL contains all the key words
	 * @param url
	 * @param paras
	 * @return
	 */
	public boolean isContain(URL url, List<String> paras) {
		for(String word : paras) {
			if(url.getURLValue().indexOf(word) == -1)
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
	public boolean isNotContain(URL url, List<String> paras) {
		for(String word : paras) {
			if(url.getURLValue().indexOf(word) != -1)
				return false;
		}
		return true;
	}
	
	public abstract boolean shouldVisit(URL url);
}

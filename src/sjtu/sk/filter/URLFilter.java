package sjtu.sk.filter;

import java.util.List;

import sjtu.sk.url.manager.URL;

public class URLFilter extends Filter {
	
	public boolean shouldVisit(URL url, List<Object> paras) {
		for(Object para : paras) {
			String word = para.toString();
			if(url.getURLValue().indexOf(word) == -1)
				return false;
		}
		return true;
	}
}

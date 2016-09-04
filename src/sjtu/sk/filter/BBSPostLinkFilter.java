package sjtu.sk.filter;

import java.util.Arrays;

import sjtu.sk.url.manager.Link;

public class BBSPostLinkFilter extends LinkFilter {
	public boolean shouldVisit(Link link) {
		if(!this.isNotContain(link, Arrays.asList("Re", "提示")))
			return false;
		if(!this.isContain(link, Arrays.asList("○")))
			return false;
		return true;
	}
	
	public static void main(String args[]) {
		System.out.println((int)'○');
	}

}

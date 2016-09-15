package sjtu.sk.filter;

import java.util.ArrayList;
import java.util.List;

import sjtu.sk.url.manager.Link;

public class HDULinkFilter extends LinkFilter {
	public boolean shouldVisit(Link link) {
		return false;
	}
	
}

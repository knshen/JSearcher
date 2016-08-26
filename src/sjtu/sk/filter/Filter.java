package sjtu.sk.filter;

import sjtu.sk.url.manager.URL;

import java.util.*;

public abstract class Filter {
	public abstract boolean shouldVisit(URL url, List<Object> paras); 
}

package filter.sjtu.sk;

import url.manager.sjtu.sk.URL;
import java.util.*;

public abstract class Filter {
	public abstract boolean shouldVisit(URL url, List<Object> paras); 
}

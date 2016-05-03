package parser.sjtu.sk;

import org.jsoup.nodes.Document;
import java.util.*;

public abstract class DataExtractor<T> {
	
	public abstract List<T> extract(Document doc);
	
}

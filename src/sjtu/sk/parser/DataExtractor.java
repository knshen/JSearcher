package sjtu.sk.parser;

import org.jsoup.nodes.Document;
import java.util.*;

public abstract class DataExtractor {
	
	public abstract List<Object> extract(Document doc);
	
}

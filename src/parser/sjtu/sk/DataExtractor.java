package parser.sjtu.sk;

import org.jsoup.nodes.Document;
import java.util.*;

public abstract class DataExtractor {
	
	public abstract List<String> extract(Document doc);
	
}

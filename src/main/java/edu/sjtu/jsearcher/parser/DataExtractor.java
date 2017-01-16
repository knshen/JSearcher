package edu.sjtu.jsearcher.parser;

import org.jsoup.nodes.Document;
import java.util.*;

public abstract class DataExtractor {
	
	public abstract List<Object> extract(Document doc, String url);
	
}

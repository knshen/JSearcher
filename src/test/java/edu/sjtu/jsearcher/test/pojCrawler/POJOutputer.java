package edu.sjtu.jsearcher.test.pojCrawler;

import java.util.*;
import java.io.*;

import edu.sjtu.jsearcher.outputer.Outputer;
import edu.sjtu.jsearcher.test.dto.OJDTO;
import edu.sjtu.jsearcher.util.Util;

public class POJOutputer extends Outputer {
	public boolean output(String task_name, List<Object> data) {
		for(Object obj : data) {
			OJDTO dto = (OJDTO)obj;
			System.out.println(dto.getTitle());
		}
		
		return true;
	}
}

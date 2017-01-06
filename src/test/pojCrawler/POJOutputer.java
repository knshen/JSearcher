package test.pojCrawler;

import java.util.*;
import java.io.*;

import sjtu.sk.outputer.Outputer;
import sjtu.sk.util.Util;
import test.dto.OJDTO;

public class POJOutputer extends Outputer {
	public boolean output(String task_name, List<Object> data) {
		for(Object obj : data) {
			OJDTO dto = (OJDTO)obj;
			System.out.println(dto.getTitle());
		}
		
		return true;
	}
}

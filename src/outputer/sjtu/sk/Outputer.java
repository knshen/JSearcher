package outputer.sjtu.sk;

import java.io.*;
import java.util.*;

/**
 * Outputer is used to persist crawled data
 * @author ShenKai
 *
 */
public abstract class Outputer {
	public abstract boolean output(String path, Date date, String task_name, String dto);
		
}

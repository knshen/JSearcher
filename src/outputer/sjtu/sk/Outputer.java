package outputer.sjtu.sk;

import java.io.*;
import java.util.*;

/**
 * Outputer is used to persist crawed data
 * @author ShenKai
 *
 */
public abstract class Outputer {
	public abstract boolean output(String path, List<String> data);
		
}

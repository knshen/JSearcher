package outputer.sjtu.sk;

import java.io.*;
import java.util.*;

/**
 * one of the outputers : persist crawed data to a new html file
 * @author ShenKai
 *
 */
public class HtmlTableOutputer extends Outputer {
	public boolean output(String path, List<String> data) {
		File file = null;
		BufferedWriter bw = null;
		
		try {
			file = new File(path);
			bw = new BufferedWriter(new FileWriter(file));
			bw.write("<html>\n");
			bw.write("<body>\n");
			bw.write("<table>\n");
			
			for(String str : data) {				
				String tmp[] = str.split("\\.");
				if(tmp.length < 2)
					continue;
				
				bw.write("<tr>\n");
				String id = tmp[0].trim();
				String title = tmp[1].trim();
				
				bw.write("<td>");
				bw.write(id);
				bw.write("</td>");
				bw.write("<td>");
				bw.write(title);
				bw.write("</td>\n");
				bw.write("</tr>\n");
				bw.flush();
			}
			
			bw.write("</table>\n");
			bw.write("</body>\n");
			bw.write("</html>\n");
			bw.flush();
			
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			try {
				bw.close();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
		}
		return true;
	}
	
	public static void main(String[] args) {
		// unit test
		Outputer out = new HtmlTableOutputer();
		out.output("/home/knshen/test.html", Arrays.asList("1.aaa", "2.bbb", "3.ccc"));

	}

}

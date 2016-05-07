package outputer.sjtu.sk;

import java.io.*;
import java.util.*;

import db.sjtu.sk.DBReader;
import dto.user.LeetCodeTitleDTO;

/**
 * one of the outputers : persist crawled data to a new html file
 * @author ShenKai
 *
 */
public class HtmlTableOutputer extends Outputer {
	public boolean output(String path, Date date, String task_name, String dto) {
		if(date == null)
			date = new Date();
		
		File file = null;
		BufferedWriter bw = null;
		
		List<Object> data = DBReader.readDataFromDB(date, task_name, dto, null, null);
		try {
			file = new File(path);
			bw = new BufferedWriter(new FileWriter(file));
			bw.write("<html>\n");
			bw.write("<body>\n");
			bw.write("<table>\n");
			
			for(Object obj : data) {
				LeetCodeTitleDTO ltdto = (LeetCodeTitleDTO)obj;
				
				bw.write("<tr>\n");
				String id = String.valueOf(ltdto.getId()).trim();
				String title = ltdto.getTitle();
				
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
	
}

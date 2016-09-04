package test;

import java.util.*;

import sjtu.sk.url.manager.URL;

public class BBSTest {

	public static void main(String[] args) {
		for(int i=6774; i>=1; i--) {
			URL url = new URL("https://bbs.sjtu.edu.cn/bbsdoc,board,JobInfo,page,"
					+ i + ".html");
		}

	}

}

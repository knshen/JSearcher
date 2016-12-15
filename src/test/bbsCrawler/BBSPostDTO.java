package test.bbsCrawler;

import java.util.Date;

public class BBSPostDTO {
	private String title;
	private String content;
	private boolean isIntern = false;
	private String url;
	private String date; // (yyyy-mm-dd)
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean getIsIntern() {
		return isIntern;
	}

	public void setIsIntern(boolean isIntern) {
		this.isIntern = isIntern;
	}

	public BBSPostDTO() {
		
	}
}

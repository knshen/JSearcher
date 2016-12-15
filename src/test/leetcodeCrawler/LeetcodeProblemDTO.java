package test.leetcodeCrawler;

public class LeetcodeProblemDTO {
	private int id;
	private String title;
	private long accept;
	private long submission;
	private String difficulty;
	private String content;
	private double acRatio;
	private String url;
	
	public double getAcRatio() {
		return acRatio;
	}

	public void setAcRatio(double acRatio) {
		this.acRatio = acRatio;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getAccept() {
		return accept;
	}

	public void setAccept(long accept) {
		this.accept = accept;
		if(accept != 0 && submission !=0)
			this.acRatio = accept / (double)submission;
	}

	public long getSubmission() {
		return submission;
	}

	public void setSubmission(long submission) {
		this.submission = submission;
		if(accept != 0 && submission !=0)
			this.acRatio = accept / (double)submission;
	}
	
	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LeetcodeProblemDTO() {
		
	}
	
	public String toString() {
		return id + ", " + title + ", " + accept + ", " + submission + 
				", " + acRatio + ", " + difficulty + ", " + content;
	}

}

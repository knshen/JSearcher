package edu.sjtu.jsearcher.test.dto;

public class OJDTO {
	private String title;
	private String content; // description + input + output + sample input + sample output
	private long submit;
	private long accept;
	private double acRatio;
	private String url;
	private int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getSubmit() {
		return submit;
	}

	public void setSubmit(long submit) {
		this.submit = submit;
		if(this.submit > 0 && this.accept > 0)
			this.acRatio = accept / (double)submit;
	}

	public long getAccept() {
		return accept;
	}

	public void setAccept(long accept) {
		this.accept = accept;
		if(this.submit > 0 && this.accept > 0)
			this.acRatio = accept / (double)submit;
	}

	public double getAcRatio() {
		return acRatio;
	}

	public void setAcRatio(double acRatio) {
		this.acRatio = acRatio;
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

	public OJDTO() {
		
	}
	
}

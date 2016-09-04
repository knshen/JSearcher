package sjtu.sk.url.manager;

public class Link extends URL {
	private String link_text;
	
	public String getLink_text() {
		return link_text;
	}

	public void setLink_text(String link_text) {
		this.link_text = link_text;
	}

	public Link(String link_text) {
		super();
		this.link_text = link_text;
	}
	
	public Link(String url, String link_text) {
		super(url);
		this.link_text = link_text;
	}
	
	public String toString() {
		return this.link_text + ": " + this.getURLValue();
	}
}

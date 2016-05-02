package url.manager.sjtu.sk;

public class URL {
	private String urlValue;
	
	public URL(String url) {
		this.urlValue = url;
	}
	
	public String getURLValue() {
		return urlValue.trim();
	}
	
	public boolean equals(Object other) {
		URL _url = (URL)other;
		return _url.urlValue.equals(urlValue);
	}
	
	public int hashCode() {
		return urlValue.hashCode();
	}
	
	public String toString() {
		return "[" + urlValue + "]";
	}
}

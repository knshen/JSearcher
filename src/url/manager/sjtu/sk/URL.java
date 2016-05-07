package url.manager.sjtu.sk;

import util.sjtu.sk.Util;

public class URL {
	private String urlValue;
	private String md5str;
	
	public URL() {
		
	}
	
	public URL(String url) {
		this.urlValue = url;
		this.md5str = Util.MD5(urlValue.trim());
	}
	
	public void setUrlValue(String urlValue) {
		this.urlValue = urlValue;
	}

	public void setMd5str(String md5str) {
		this.md5str = md5str;
	}

	public String getMD5Str() {
		return this.md5str;
	}
	
	public String getURLValue() {
		return urlValue.trim();
	}
	
	public boolean equals(Object other) {
		URL _url = (URL)other;
		return _url.md5str.equals(md5str);
	}
	
	public int hashCode() {
		return md5str.hashCode();
	}
	
	public String toString() {
		return md5str;
	}
	
	public String print() {
		return "[" + urlValue + "]";
	}
}

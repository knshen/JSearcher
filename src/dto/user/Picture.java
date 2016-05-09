package dto.user;

public class Picture {
	private String filePath;
	private String urlPath;
	
	public Picture() {
		
	}
	
	public Picture(String fp, String up) {
		this.filePath = fp;
		this.urlPath = up;
	}

	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getUrlPath() {
		return urlPath;
	}
	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}
	
}

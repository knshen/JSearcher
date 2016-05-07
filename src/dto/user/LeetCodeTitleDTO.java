package dto.user;

public class LeetCodeTitleDTO {
	private int id;
	private String title;
	
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

	public LeetCodeTitleDTO() {
		
	}
	
	public LeetCodeTitleDTO(int id, String title) {
		this.id = id;
		this.title = title;
	}
	
	public String toString() {
		return "[" + id + " -> " + title + "]";
	}
}

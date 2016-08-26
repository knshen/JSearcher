package sjtu.sk.mail;

import java.util.Date;

public class MailMessage {
	private String fromAddr;
	private String toAddr;
	private String subject;
	private String content;
	private Date sentDate;
	private Date recvDate;
	
	public String getFromAddr() {
		return fromAddr;
	}
	
	public void setFromAddr(String fromAddr) {
		this.fromAddr = fromAddr;
	}
	
	public String getToAddr() {
		return toAddr;
	}
	
	public void setToAddr(String toAddr) {
		this.toAddr = toAddr;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Date getSentDate() {
		return sentDate;
	}
	
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}
	
	public Date getRecvDate() {
		return recvDate;
	}
	
	public void setRecvDate(Date recvDate) {
		this.recvDate = recvDate;
	}
	
	public String toString() {
		return fromAddr + " -> " + toAddr + "\n"
				+ sentDate + " -> " + recvDate + "\n"
				+ subject + "\n"
				+ content;
	}
	
}

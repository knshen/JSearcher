package edu.sjtu.jsearcher.mail;

import java.io.IOException;
import java.util.*;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MailUtil {
	
	public static boolean sendTextMail(String account, String password, MailMessage mm) {
		String tmp[] = account.split("@");
		String name = tmp[0];
		
		Properties prop = new Properties();
        prop.put("mail.smtp.ssl.enable", true);
        prop.put("mail.smtp.port", 465);
        prop.setProperty("mail.host", "smtp." + tmp[1]);
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
            
        Session session = Session.getDefaultInstance(prop);
        session.setDebug(true);
        Transport ts = null;
        
        try {
            ts = session.getTransport();
            ts.connect("smtp." + tmp[1], name, password);
            
            MimeMessage message = new MimeMessage(session);
    	    message.setFrom(new InternetAddress(mm.getFromAddr()));   	      
    	    message.setRecipient(Message.RecipientType.TO, new InternetAddress(mm.getToAddr()));
    	        
    	    message.setSubject(mm.getSubject());	       
    	    message.setContent(mm.getContent(), "text/html;charset=UTF-8");

            ts.sendMessage(message, message.getAllRecipients());
        } catch(NoSuchProviderException npe) {
        	npe.printStackTrace();
        	return false;
        } catch(MessagingException me) {
        	me.printStackTrace();
        	return false;
        } 
        finally {
        	try {
            	if(ts != null)
            		ts.close();
        	} catch(MessagingException me) {
        		me.printStackTrace();
        		return false;
        	}
        }
        return true;
	}
	
	public static List<MailMessage> getInboxMessage(String account, String password) {
		List<MailMessage> res = new ArrayList<MailMessage>();
		String tmp[] = account.split("@");
		
		Properties props = new Properties();
	    props.put("mail.pop3.ssl.enable", true);
	    props.put("mail.pop3.host", "pop." + tmp[1]);
	    props.put("mail.pop3.port", 995);

	    Session session = Session.getDefaultInstance(props);

	    Store store = null;
	    Folder folder = null;
	    try {
	        store = session.getStore("pop3");
	        store.connect(account, password);

	        folder = store.getFolder("INBOX");
	        folder.open(Folder.READ_ONLY);
	       
	        Message[] msgs = folder.getMessages();
	        for(Message message : msgs) {
	        	MailMessage mm = new MailMessage();
	        	mm.setSentDate(message.getSentDate());
	        	mm.setRecvDate(message.getReceivedDate());
	        	mm.setSubject(message.getSubject());
	        	mm.setFromAddr(message.getFrom()[0].toString());
	        	mm.setToAddr(message.getReplyTo()[0].toString());
	        	mm.setContent(message.getContent().toString());
		 
		        res.add(mm);
	        }
	        
	    } catch (NoSuchProviderException e) {
	        e.printStackTrace();
	        return null;
	    } catch (MessagingException e) {
	        e.printStackTrace();
	        return null;
	    } catch(IOException ioe) {
	    	ioe.printStackTrace();
	    	return null;
	    }
	    finally {
	        try {
	            if (folder != null) 
	                folder.close(false);
	            
	            if (store != null) 
	                store.close();
	            
	        } catch (MessagingException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    return res;
	}
	
	public static void main(String[] args) {
		List<MailMessage> list = MailUtil.getInboxMessage("k.shen@qq.com", "knshen530530");
		for(MailMessage mm : list) {
			System.out.println(mm);
			System.out.println("----------");
		}
	}

}

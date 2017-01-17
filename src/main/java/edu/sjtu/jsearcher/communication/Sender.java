package edu.sjtu.jsearcher.communication;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import java.util.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Sender {
	private ConnectionFactory connectionFactory = null;
	private Connection connection = null;
	private Session session = null;
	private Destination destination = null;
	private MessageProducer producer = null;
	
	/**
	 * 
	 * @param queue_name : name of destination message queue
	 * @param host_name : destination ip/host name
	 */
	public Sender(String queue_name, String host_name) {
		connectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD,
                "tcp://" + host_name +":61616");
		
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Boolean.TRUE,
                    Session.AUTO_ACKNOWLEDGE);      
            destination = session.createQueue(queue_name);
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            
        } catch (JMSException je) {
            je.printStackTrace();
        } 

	}
	
	/**
	 * send messages one by one
	 * @param msgs
	 */
	public void sendMsg(List<String> msgs) {
		try {
	        for(String msg : msgs) {
	            TextMessage message = session
	                    .createTextMessage(msg);
	            
	            //Logging.log("Thread: " + Thread.currentThread() + "  send msg: " + msg + "\n");
	            producer.send(message);
	        } // end for
			session.commit();
			
		} catch(JMSException je) {
			je.printStackTrace();
		}
	}
    
	public void close() {
		 try {
             if (null != connection)
                 connection.close();
         } catch (JMSException je) {
        	 je.printStackTrace();
         }
	}
	
    public static void main(String[] args) {
    	Sender sender = new Sender("FirstQueue", "192.168.1.150");
    	List<String> msgs = new ArrayList<String>();
    	msgs.add("cat");
    	msgs.add("mouse");
    	msgs.add("dog");
    	msgs.add("bull");
    	msgs.add("tiger");
    	
        sender.sendMsg(msgs);
        
        sender.close();
    }

}
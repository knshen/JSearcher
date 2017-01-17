package edu.sjtu.jsearcher.communication;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public abstract class Receiver implements Runnable {
	public static final int interval = 500;
	
    private ConnectionFactory connectionFactory = null;
    private Connection connection = null;
    private Session session = null;
    private Destination destination = null;
    private MessageConsumer consumer = null;
         
	public Receiver(String queue_name) {	
		connectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD,
                "tcp://localhost:61616");
		try {
			connection = connectionFactory.createConnection();
	        connection.start();
	        session = connection.createSession(Boolean.FALSE,
	                Session.AUTO_ACKNOWLEDGE);
	        destination = session.createQueue(queue_name);
	        consumer = session.createConsumer(destination);

		} catch(JMSException je) {
			je.printStackTrace();
		}
	}
	
	/**
	 * business logic after getting messages
	 * @param msg
	 */
	public abstract void afterRecvMsg(String msg); 
	
	public void run() {
		try {
			while(true) {
				// check message queue every interval
	            TextMessage message = (TextMessage) consumer.receive(interval);
	            if (null != message) {
	                String msg = message.getText();
	                //Logging.log("receive msg: " + msg + "\n");
	                afterRecvMsg(msg);
	            } 
	       
			}
          
		} catch(JMSException je) {
			je.printStackTrace();
		} finally {
			try {
				if (null != connection)
                    connection.close();
			} catch(JMSException je) {
				je.printStackTrace();
			}
        }

	}
	
    
}

package edu.sjtu.jsearcher.storage;

import java.util.*;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;

import edu.sjtu.jsearcher.util.PersistentStyle;

/**
 * MemoryDataWriter is used to periodically flush in-memory data to DB/ES to
 * prevent "out of memory" exception
 * 
 * every CHECK_PERIOD, it flushes data to DB/ES
 * @author Kai
 *
 */
public class MemoryDataWriter extends Thread {
	private static Logger logger = Logger.getLogger(MemoryDataWriter.class); 
	
	// time interval between two write operations
	public static final int CHECK_PERIOD = 10000; 
	// size threshold to trigger flush operation
	// public static final long SIZE_THRESHOLD = 100; 

	private List<Object> data = null;
	private String dto;
	private String task_name;
	private int persistent_style;
	private Lock lock = null;

	public MemoryDataWriter(Lock lock, List<Object> data, String dto,
			String task_name, int persistent_style) {
		this.lock = lock;
		this.data = data;
		this.dto = dto;
		this.task_name = task_name;
		this.persistent_style = persistent_style;
	}

	public void run() {
		// if data is not saved to ES/DB, do nothing
		if(this.persistent_style == PersistentStyle.OTHER)
			return;
		while (!interrupted()) {
			try {
				Thread.sleep(CHECK_PERIOD);
				
				List<Object> c_data = null;
				lock.lock();	
				try {
					if(data == null || data.size() == 0)
						continue;
					c_data = new ArrayList<Object>(data);
					data = new ArrayList<Object>();
					
				} finally {
					lock.unlock();
				}
				
				if(c_data.size() > 0) {
					logger.info("Thread writer: before writing, size: " + c_data.size());
					if (this.persistent_style == PersistentStyle.MONGO)
						DataWriter.writeData2MongoDB(c_data, task_name, dto);
					else if(this.persistent_style == PersistentStyle.MYSQL)
						DataWriter.writeData2MySQL(c_data, task_name, dto);
					else if(this.persistent_style == PersistentStyle.ES)
						DataWriter.writeData2ES(c_data, task_name, dto);
					logger.info("Thread writer: finish writing!");
				}

			} catch (InterruptedException ie) {
				//ie.printStackTrace();
				logger.info("writer thread is interrupted!");
				break;
			}

		}
	}

}

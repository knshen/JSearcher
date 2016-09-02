package sjtu.sk.storage;

import java.util.*;
import java.util.concurrent.locks.Lock;

import sjtu.sk.util.PersistentStyle;

/**
 * MemoryDataWriter is used to periodically flush in-memory data to DB/ES to
 * prevent "out of memory" exception
 * 
 * @author Kai
 *
 */
public class MemoryDataWriter implements Runnable {
	// time interval between two check operations
	public static final int CHECK_PERIOD = 3000; 
	// size threshold to trigger flush operation
	public static final long SIZE_THRESHOLD = 100; 

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
		while (true) {
			if (data.size() >= SIZE_THRESHOLD) {
				lock.lock();	
				try {
					if (this.persistent_style == PersistentStyle.DB)
						DataWriter.writeData2DB(data, task_name, dto);
					else
						DataWriter.writeData2ES(data, task_name, dto);
					data = new ArrayList<Object>();
				} finally {
					lock.unlock();
				}
			}

			try {
				Thread.sleep(CHECK_PERIOD);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		

	}

}

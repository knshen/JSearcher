package demo;

import java.util.concurrent.*;

class MyThread implements Runnable {
	String name = "";
	public MyThread(String name) {
		this.name = name;
	}
	
	public void run() {
		for(int i=1; i<=3; i++) {
			System.out.println(this.name + " " + i);
		}
	}
}

public class ThreadPoolDemo {

	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(3);
		for(int i=0; i<10; i++) {
			Thread task = new Thread(new MyThread("task"+(i+1)));
			pool.execute(task);
		}

		pool.shutdown();
	}

}

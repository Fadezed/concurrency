package com.example.concurrency.dflockTest;

import java.util.concurrent.CountDownLatch;


public class SyncTest {
	private static int count;

	private static int readThreadNum = 10;
	private static int writeThreadNum = 0;

	private static int maxValue = 1000;
	private static String lock = "lock";

	public static void main(String[] args) {
		Counter lockTest = new SyncTest().new Counter();
		long startTime = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch(readThreadNum + writeThreadNum);
		for (int i = 0; i < writeThreadNum; i++) {
			new Thread(() -> {
				for (int cur = 0; cur < maxValue; cur++) {
					lockTest.count();
				}
				latch.countDown();
			}).start();
		}
		
		for (int i = 0; i < readThreadNum; i++) {
			new Thread(() -> {
				for (int cur = 0; cur < maxValue; cur++) {
					lockTest.get();
				}
				latch.countDown();
			}).start();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();

		System.out.println("LockTest执行时长：" + (endTime - startTime) + ", count" + count);

	}

	class Counter {

	
		public int get() {

			synchronized(lock) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
             	return count;
             	//System.out.println("count：" + count);
             }
		}

		public void count() {
			synchronized(lock) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
             	count++;
             	//System.out.println("count：" + count);
             }
		}
	}

}

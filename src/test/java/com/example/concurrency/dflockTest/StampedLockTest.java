package com.example.concurrency.dflockTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.StampedLock;

public class StampedLockTest {
	private static int count;

	private static int readThreadNum = 10;
	private static int writeThreadNum = 0;

	private static int maxValue = 10000;
	private final StampedLock s1 = new StampedLock();

	public static void main(String[] args) {
		Counter lockTest = new StampedLockTest().new Counter();
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
			 //乐观锁操作
	        long stamp = s1.tryOptimisticRead();  
	        //拷贝变量
	        try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        int currentC = count;
	        //判断读期间是否有写操作
	        if (!s1.validate(stamp)) {
	            //
	            stamp = s1.readLock();
	            try {
	            	currentC = count;
	            } finally {
	                s1.unlockRead(stamp);
	            }
	        }
	        return currentC;
		}

		public void count() {
			long stamp = s1.writeLock();
			try {
				Thread.sleep(1);

				count++;
				// System.out.println("count：" + count);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				s1.unlockWrite(stamp);
			}
		}
		
		
	}
}

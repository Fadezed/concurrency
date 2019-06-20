package com.example.concurrency.dflockTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

public class LongAdderTest {
	private static LongAdder count = new LongAdder();


	private static int readThreadNum = 10;
	private static int writeThreadNum = 0;

	private static int maxValue = 1000;

	public static void main(String[] args) {
		Counter lockTest = new LongAdderTest().new Counter();
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


		public long get() {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return  count.sum();
		}

		public void count() {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count.increment();
		}
	}
}

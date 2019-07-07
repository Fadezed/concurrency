package com.example.concurrency.patterns.conditionqueues;

import com.example.concurrency.features.threadPool.ThreadPoolBuilder;

import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 描述:
 * 条件队列
 * 通过ReentrantLock 实现打印和停止打印的条件队列，每次容量为5
 *
 * @author zed
 * @since 2019-07-01 10:59 AM
 */
public class ConditionQueue {
    private static final int LIMIT = 5;
    private int messageCount = 0;
    private Lock lock = new ReentrantLock();
    private Condition limitReachedCondition = lock.newCondition();
    private Condition limitUnreachedCondition = lock.newCondition();
    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.fixedPool().setPoolSize(2).setThreadNamePrefix("Condition Queue").build();

    /**
     * 停止打印
     * @throws InterruptedException e
     */
    private void stopMessages() throws InterruptedException {
        lock.lock();
        try {
            while (messageCount < LIMIT) {
                limitReachedCondition.await();
            }
            System.err.println("Limit reached. Wait 2s");
            Thread.sleep(2000);
            messageCount = 0;
            limitUnreachedCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 打印
     * @param message mes
     * @throws InterruptedException e
     */
    private void printMessages(String message) throws InterruptedException {
        lock.lock();
        try {
            while (messageCount == LIMIT) {
                limitUnreachedCondition.await();
            }
            System.out.println(message);
            messageCount++;
            limitReachedCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ConditionQueue queue = new ConditionQueue();
        // Will run indefinitely
        threadPoolExecutor.execute(() -> {
            while (true) {
                String uuidMessage = UUID.randomUUID().toString();
                try {
                    queue.printMessages(uuidMessage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadPoolExecutor.execute(() -> {
            while (true) {
                try {
                    queue.stopMessages();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}


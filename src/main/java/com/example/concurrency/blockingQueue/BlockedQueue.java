package com.example.concurrency.blockingQueue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 描述:
 * 阻塞队列
 *
 * @author zed
 * @since 2019-06-13 5:15 PM
 */
public class BlockedQueue<T>{

    /** The queued items */
    private final Object[] items;
    /** Number of elements in the queue */
    private int count;
    private final Lock lock = new ReentrantLock();
    /**
     * 条件变量：队列不满
     */
    private final Condition notFull;
    /**
     * 条件变量：队列不空
     */
    private final Condition notEmpty;

    public BlockedQueue(int capacity) {
        if (capacity <= 0){
            throw new IllegalArgumentException();
        }
        this.items = new Object[capacity];
        notEmpty = lock.newCondition();
        notFull =  lock.newCondition();
    }

    /**
     * 入队
     * @param x x
     * @throws InterruptedException ex
     */
    void enq(T x) throws InterruptedException{
        lock.lock();
        try {
            while (count ==items.length){
                // 等待队列不满
                notFull.await();
            }
            // 省略入队操作...
            // 入队后, 通知可出队
            notEmpty.signal();
        }finally {
            lock.unlock();
        }
    }

    /**
     * 出队
     */
    void deq()throws InterruptedException{
        lock.lock();
        try {
            while (count == 0){
                // 等待队列不空
                notEmpty.await();
            }
            // 省略出队操作...
            // 出队后，通知可入队
            notFull.signal();
        }finally {
            lock.unlock();
        }
    }
}



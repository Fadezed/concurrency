package com.example.concurrency.features.readWriteLock;

import java.util.concurrent.locks.StampedLock;

/**
 * 描述:
 * StampedLockEx
 * 写锁和悲观锁 跟ReentrantReadWriteLock 类似，非可重入锁，并且不支持条件变量
 * ReadWriteLock 支持多个线程同时读，但是当多个线个线程同时读的时候，
 * 所有的写操作会被阻塞；而 StampedLock 提供的乐观读（无锁），是允许一个线程获取写锁的，也就是说不是所有的写操作都被阻塞。
 *
 * 不能直接使用interrupt 会导致cpu100%
 * 若想中断可以使用
 * readLockInterruptibly() 和writeLockInterruptibly()
 *
 * @author zed
 * @since 2019-06-17 5:24 PM
 */
public class StampedLockEx {

    private final StampedLock sl = new StampedLock();
    /**
     * 获取 / 释放悲观读锁
     * @return o
     */
    Object get(){
        long stamp = sl.readLock();
        try {
            return new Object();
            // 省略业务相关代码
        } finally {
            sl.unlockRead(stamp);
        }

    }

    /**
     * 获取 / 释放写锁
     */
    void set(){
        long stamp = sl.writeLock();
        try {
            // 省略业务相关代码
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    /**
     * 乐观读升级官方示例
     */
    static class Point {
        private int x, y;
        final StampedLock sl = new StampedLock();

        /**
         * 计算到原点的距离
         * @return v
         */
        Double distanceFromOrigin() {
            // 乐观读
            long stamp = sl.tryOptimisticRead();
            // 读入局部变量，
            // 读的过程数据可能被修改
            int curX = x, curY = y;
            // 判断执行读操作期间，
            // 是否存在写操作，如果存在，
            // 则 sl.validate 返回 false
            if (!sl.validate(stamp)){
                // 升级为悲观读锁
                stamp = sl.readLock();
                try {
                    curX = x;
                    curY = y;
                } finally {
                    // 释放悲观读锁
                    sl.unlockRead(stamp);
                }
            }
            return Math.sqrt(curX * curX + curY * curY);
        }
    }




}


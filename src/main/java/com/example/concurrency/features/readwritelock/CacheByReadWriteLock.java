package com.example.concurrency.features.readwritelock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 描述:
 * 读写锁实现缓存
 *
 * ReentrantReadWriteLock 只支持锁的降级 不支持锁升级 并且只有写锁可以创建条件变量
 * 读写锁允许多个线程同时读共享变量，当一个线程在写共享变量的时候，是不允许其他线程执行写操作和读操作。
 *
 *
 * @author zed
 * @since 2019-06-17 5:00 PM
 */
public class CacheByReadWriteLock<K,V> {
    private final Map<K, V> m = new HashMap<>();

    private final ReadWriteLock rwl = new ReentrantReadWriteLock();
    /**
     * 读锁
     */
    private final Lock readLock = rwl.readLock();
    /**
     * 写锁
     */
    private final Lock writeLock = rwl.writeLock();

    /**
     * 写
     * @param key k
     * @param v v
     * @return v
     */
    V put(K key, V v) {
        writeLock.lock();
        try {
            return m.put(key, v);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 读
     * @param key key
     * @return v
     */
    V get1(K key) {
        readLock.lock();
        try {
            return m.get(key);
        } finally {
            readLock.unlock();
        }

    }

    /**
     * 按需加载
     * @param key key
     * @return v
     */
    V get2(K key) {
        V v = null;
        // 读缓存
        readLock.lock();
        try {
            v = m.get(key);
        } finally{
            readLock.unlock();
        }
        // 缓存中存在，返回
        if(v != null) {
            return v;
        }
        // 缓存中不存在，查询数据库
        writeLock.lock();
        try {
            // 再次验证
            // 并发情况下其他线程可能已经查询过数据库
            v = m.get(key);
            if(v == null){
                // 查询数据库
                m.put(key, v);
            }
        } finally{
            writeLock.unlock();
        }
        return v;
    }


}


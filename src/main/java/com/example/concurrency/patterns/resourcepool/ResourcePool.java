package com.example.concurrency.patterns.resourcepool;

import com.example.concurrency.features.threadPool.ThreadPoolBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 描述:
 * 对象资源池
 * 适用场景:当要创建某些有限资源的池时使用
 * @author zed
 * @since 2019-07-01 3:01 PM
 */
public class ResourcePool<T> {
    private final static TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private Semaphore semaphore;
    private BlockingQueue<T> resources;

    public ResourcePool(int poolSize, List<T> initializedResources) {
        //fail true 即 FIFO
        this.semaphore = new Semaphore(poolSize, true);
        this.resources = new LinkedBlockingQueue<>(poolSize);
        this.resources.addAll(initializedResources);
    }

    /**
     * 获取资源 阻塞等待
     * @return resource
     * @throws InterruptedException e
     */
    public T get() throws InterruptedException {
        return get(Integer.MAX_VALUE);
    }

    /**
     * 获取资源
     * @param secondsToTimeout 超时时间
     * @return source
     * @throws InterruptedException e
     */
    public T get(long secondsToTimeout) throws InterruptedException {
        semaphore.acquire();
        try {
            return resources.poll(secondsToTimeout, TIME_UNIT);
        } finally {
            semaphore.release();
        }
    }

    /**
     * 释放资源
     * @param resource re
     * @throws InterruptedException e
     */
    public void release(T resource) throws InterruptedException {
        if (resource != null) {
            resources.put(resource);
            semaphore.release();
        }
    }

    public static void main(String[] args) {
        ThreadPoolExecutor executor = ThreadPoolBuilder.cachedPool().setThreadNamePrefix("资源池线程").build();
        ResourcePool<Integer> pool = new ResourcePool<>(15, Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14));
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            executor.execute(() -> {
                try {
                    Integer value = pool.get(60);
                    System.out.println("Value taken: " + value);
                    Thread.sleep(random.nextInt(5000));
                    pool.release(value);
                    System.out.println("Value released " + value);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
    }
}


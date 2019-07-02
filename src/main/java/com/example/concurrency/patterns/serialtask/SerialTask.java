package com.example.concurrency.patterns.serialtask;

import com.example.concurrency.features.threadPool.ThreadPoolBuilder;
import com.example.concurrency.util.ThreadUtil;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 * 通过单锁和volatile 实现的线程共享方案
 * 适用场景：特定条件下有效阻止并行执行
 *
 * @author zed
 * @since 2019-07-02 3:33 PM
 */
public class SerialTask {
    /**
     * 导出flag true：有任务在执行 false：可以执行当前任务
     */
    private static volatile boolean permits =false;

    /**
     * 占用任务
     */
    public synchronized static boolean setPermits(){
        if(!permits){
            permits = true;
            return true;
        }
        return false;
    }
    /**
     * 释放任务
     */
    public synchronized static void releasePermits() {
        permits = false;
    }
    public static void main(String[] args) throws InterruptedException{
        ThreadPoolExecutor pool = ThreadPoolBuilder.cachedPool().setThreadNamePrefix("导出excel").build();
        for(int i =0;i< 10;i++) {
            pool.execute(() -> {
                while (SerialTask.permits || !SerialTask.setPermits()) {
                    //return or retry
                }
                SerialTask.setPermits();
                System.out.println(Thread.currentThread().getName()+"正在执行");
                ThreadUtil.sleep(1000);
                System.out.println(Thread.currentThread().getName()+"执行结束");
                SerialTask.releasePermits();
            });
        }
        pool.awaitTermination(5, TimeUnit.SECONDS);

    }
}


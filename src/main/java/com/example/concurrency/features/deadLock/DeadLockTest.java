package com.example.concurrency.features.deadLock;

import com.example.concurrency.features.threadPool.ThreadPoolBuilder;
import com.example.concurrency.util.ThreadDumpHelper;
import com.example.concurrency.util.ThreadUtil;

import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author zed
 * 死锁测试
 * 避免死锁常用方法
 * 1、避免一个线程同时获取多个锁
 * 2、避免一个线程在锁内同时占用多个资源，尽量保证每个锁只占用一个资源
 * 3、尝试使用定时锁，使用lock.tryLock(timeOut) 来替代使用内部锁机制
 * 4、对于数据库锁，加锁和解锁必须在一个数据库连接里，否则会出现解锁失败的情况
 *
 */
public class DeadLockTest {
    private ThreadDumpHelper dumpHelper = new ThreadDumpHelper();
    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.fixedPool().setPoolSize(2).setThreadNamePrefix("死锁测试线程").build();

    /**
     * 事实上String 对象不建议当成锁的对象（常量池的存在会导致锁重复）
     * 这里故意将A、B放入堆中以示问题
     */
    private static final String A = new String("A");
    private static final String B = new String("B");

    public static void main(String[] args) {
        new DeadLockTest().deadLock();
    }

    private void deadLock() {
        threadPoolExecutor.execute(() -> {
            synchronized (A){
                ThreadUtil.sleep(2);
                synchronized (B){
                    System.out.println("1");
                }
            }
        });

        threadPoolExecutor.execute(() -> {
            synchronized (B) {
                synchronized (A) {
                    System.out.println("2");
                }
            }
        });
        threadPoolExecutor.shutdown();
//        //获取最终状态，可能会出现线程T1 状态为TIMED_WAITING 状态（因为sleep）
        ThreadUtil.sleep(2);
        dumpHelper.tryThreadDump();
    }
}

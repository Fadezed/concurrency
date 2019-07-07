package com.example.concurrency.patterns.resourcesinitialization;

import com.example.concurrency.features.threadPool.ThreadPoolBuilder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述:
 * 基于CountDownLatch实现应用初始化
 * 适用场景:多个重要资源的初始化
 *
 * @author zed
 * @since 2019-07-01 11:17 AM
 */
public class ControlledInitialization {
    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.fixedPool()
            .setPoolSize(3)
            .setThreadNamePrefix("ControlledInitialization").build();
    /**
     * 资源1
     */
    private static class Resource1 {
    }
    /**
     * 资源2
     */
    private static class Resource2 {
    }
    /**
     * 资源3
     */
    private static class Resource3 {
    }
    private Resource1 resource1;
    private Resource2 resource2;
    private Resource3 resource3;
    /**
     * 声明一个count为3 的CountDownLatch
     */
    private CountDownLatch latch = new CountDownLatch(3);
    private Runnable initResource1 = () -> {
        try {
            // simulate wait
            Thread.sleep(4000);
            resource1 = new Resource1();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            latch.countDown();
        }
    };

    private Runnable initResource2 = () -> {
        try {
            // simulate wait
            Thread.sleep(4000);
            resource2 = new Resource2();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            latch.countDown();
        }
    };

    private Runnable initResource3 = () -> {
        try {
            // simulate wait
            Thread.sleep(4000);
            resource3 = new Resource3();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            latch.countDown();
        }
    };

    /**
     * 构造函数中实现完成加载过程
     */
    private ControlledInitialization() {
        //初始化操作
        initialize();
        //等待资源加载完毕
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*
         * 基于资源加载完毕开始后续操作
         */
        doTask();
    }
    private void doTask() {
        System.out.println("=== Resources Initialized ===");
        System.out.println("Resource 1 instance " + resource1);
        System.out.println("Resource 2 instance " + resource2);
        System.out.println("Resource 3 instance " + resource3);

    }
    private void initialize() {
        System.out.println("=== Initializing Resources ===");
        threadPoolExecutor.execute(initResource1);
        threadPoolExecutor.execute(initResource2);
        threadPoolExecutor.execute(initResource3);
        threadPoolExecutor.shutdown();
    }
    public static void main(String[] args) {
        new ControlledInitialization();
    }

}


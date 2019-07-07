package com.example.concurrency.patterns.taskconvergence;

import com.example.concurrency.features.threadPool.ThreadPoolBuilder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 描述:
 * 聚合任务     CyclicBarrier(int parties, Runnable barrierAction) barrierAction为到达屏障后优先执行的任务
 * 适用场景:当需要确定一组正在运行的任务是否已完成时
 * @author zed
 * @since 2019-06-30 3:33 PM
 */
public class TaskConvergence {

    private static final int BOUND = 150_000;
    private static final int SIZE = 400_000;
    private static final int CORES = Runtime.getRuntime().availableProcessors();
    private CyclicBarrier barrier;
    private List<Long> synchronizedLinkedList;
    private ExecutorService executor;

    @SuppressWarnings("unchecked")
    private Runnable run = () -> {
        Random random = new Random();
        List results = new LinkedList<Long>();
        for (int i = 0; i < SIZE; i++) {
            Long next = (long) random.nextInt(BOUND);
            results.add(next);
        }
        try {
            synchronizedLinkedList.addAll(results);
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    };

    /**
     * 构造函数
     */
    public TaskConvergence() {
        /*
         * 到达屏障执行任务
         */
        Runnable onComplete = () -> {
            System.out.println("=== Random Number Results ===");
            System.out.println("CPU Cores: " + CORES);
            System.out.println("Random Bound: " + BOUND);
            System.out.println("Iterations per Core: " + SIZE);
            System.out.println("Total Iterations: " + SIZE * CORES);
            System.out.println("Size: " + synchronizedLinkedList.size());
            System.out.println("Sum " + synchronizedLinkedList.stream().mapToLong(Long::longValue).sum());
        };
        barrier = new CyclicBarrier(CORES, onComplete);
        synchronizedLinkedList = Collections.synchronizedList(new LinkedList<>());
        executor = ThreadPoolBuilder.fixedPool().setPoolSize(CORES).build();
    }

    /**
     * run
     */
    public void run() {
        for (int i = 0; i < CORES; i++) {
            executor.execute(run);
        }
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
            executor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new TaskConvergence().run();
    }
}


package com.example.concurrency.features.futureTask;

import com.example.concurrency.features.threadPool.ThreadPoolBuilder;
import com.example.concurrency.features.threadPool.ThreadPoolUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 * FutureTaskEx
 *
 * @author zed
 * @since 2019-06-18 4:52 PM
 */
public class FutureTaskEx {
    /**
     * FutureTask由线程池执行
     */
    private static void exeForPool(){
        // 创建 FutureTask
        FutureTask<Integer> futureTask = new FutureTask<>(()-> 1+2);
        // 创建线程池
        ThreadPoolExecutor executor = ThreadPoolBuilder.fixedPool().build();

        try{
            // 提交 FutureTask
            executor.submit(futureTask);
            // 获取计算结果
            Integer result = futureTask.get();
            System.out.println(result);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            ThreadPoolUtil.gracefulShutdown(executor,1);

        }
    }

    /**
     * FutureTask由线程处理
     */
    private static void exeForThread(){
        // 创建 FutureTask
        FutureTask<Integer> futureTask = new FutureTask<>(()-> 1+2);
        // 创建并启动线程
        Thread T1 = new Thread(futureTask);
        T1.start();
        // 获取计算结果
        try{
            Integer result = futureTask.get();
            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 利用FutureTask实现烧水泡茶
     */
    private static void fireWater(){

        // 创建任务 T2 的 FutureTask
        FutureTask<String> ft2 = new FutureTask<>(new T2Task());
        // 创建任务 T1 的 FutureTask
        FutureTask<String> ft1 = new FutureTask<>(new T1Task(ft2));
        // 线程 T1 执行任务 ft1
        Thread t1 = new Thread(ft1);
        t1.start();
        // 线程 T2 执行任务 ft2
        Thread t2 = new Thread(ft2);
        t2.start();
        // 等待线程 T1 执行结果
        try{
            System.out.println(ft1.get());

        }catch (Exception e){
            e.printStackTrace();
        }



    }
    /**
     * 洗水壶、烧开水、泡茶
     */
    static class T1Task implements Callable<String> {
        FutureTask<String> ft2;
        T1Task(FutureTask<String> ft2){
            this.ft2 = ft2;
        }
        @Override
        public String call() throws Exception {
            System.out.println("T1: 洗水壶...");
            TimeUnit.SECONDS.sleep(1);

            System.out.println("T1: 烧开水...");
            TimeUnit.SECONDS.sleep(15);
            // 获取 T2 线程的茶叶
            String tf = ft2.get();
            System.out.println("T1: 拿到茶叶:"+tf);

            System.out.println("T1: 泡茶...");
            return " 上茶:" + tf;
        }
    }
    /**
     * 洗茶壶、洗茶杯、拿茶叶
     */
    static class T2Task implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("T2: 洗茶壶...");
            TimeUnit.SECONDS.sleep(1);

            System.out.println("T2: 洗茶杯...");
            TimeUnit.SECONDS.sleep(2);

            System.out.println("T2: 拿茶叶...");
            TimeUnit.SECONDS.sleep(1);
            return " 龙井 ";
        }
    }
    public static void main(String[] args) {
        exeForPool();
        exeForThread();
        fireWater();
    }
}


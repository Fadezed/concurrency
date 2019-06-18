package com.example.concurrency.futureTask;

import com.example.concurrency.threadPool.ThreadPoolBuilder;
import com.example.concurrency.threadPool.ThreadPoolUtil;

import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

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

    public static void main(String[] args) {
        exeForPool();
        exeForThread();
    }
}


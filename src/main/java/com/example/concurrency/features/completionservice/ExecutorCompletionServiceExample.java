package com.example.concurrency.features.completionservice;

import com.example.concurrency.features.threadPool.ThreadPoolBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 描述:
 * CompletionService BlockingQueue+Executor
 *
 * 批量执行异步任务 并且可以支持Forking Cluster模式即并行查询多个服务其中一个返回则结束
 * @author zed
 * @since 2019-07-03 10:07 AM
 */
public class ExecutorCompletionServiceExample {

    private Integer exe()throws InterruptedException{
        // 创建线程池
        ThreadPoolExecutor pool = ThreadPoolBuilder.fixedPool().setPoolSize(3).build();
        // 创建 CompletionService
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(pool);
        // 用于保存 Future 对象
        List<Future<Integer>> futures = new ArrayList<>(3);
        // 提交异步任务，并保存 future 到 futures
        futures.add(completionService.submit(this::getCoderByS1));
        futures.add(completionService.submit(this::getCoderByS2));
        futures.add(completionService.submit(this::getCoderByS3));
        // 获取最快返回的任务执行结果
        Integer r = 0;
        try {
            // 只要有一个成功返回，则 break 这里因为要判断是否是正确的结果 所以会循环三次，如果不考虑结果正确性，第一次拿出来的就是最先执行完的结果
            for (int i = 0; i < 3; ++i) {
                try {
                    r = completionService.take().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                // 简单地通过判空来检查是否成功返回
                if (r != null ) {
                    break;
                }
            }
        } finally {
            // 取消所有任务
            for(Future<Integer> f : futures){
                f.cancel(true);

            }
        }
        pool.shutdown();
        // 返回结果
        return r;

    }
    private Integer getCoderByS1(){
        return null;
    }
    private Integer getCoderByS2(){
        return 2;
    }
    private Integer getCoderByS3(){
        return 3;
    }

    public static void main(String[] args) throws InterruptedException{
        System.out.println(new ExecutorCompletionServiceExample().exe());
    }

}


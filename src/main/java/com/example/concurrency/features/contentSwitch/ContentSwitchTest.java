package com.example.concurrency.features.contentSwitch;

import com.example.concurrency.features.threadPool.ThreadPoolBuilder;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zed
 * 上下文切换测试类
 * 1、结论 循环数量在百万级别时 并发处理速度才会领先，说明上下文切换的存在以及线程创建的成本
 * 2、使用Lmbench3 测量上下文切换时长，使用vmstat 查看测量上下文切换次数上下文速度 1000次/s
 * 3、减少上下文切换
 *  1）无锁并发编程-》多线程锁竞争会引起切换，可以通过数据ID按照Hash算法取模分段 不同线程处理不同数据避免使用锁
 *  2）CAS算法
 *  3）协程：在单线程里实现多任务调度，并在单线程里维持多个任务的切换
 *  4)使用最少线程
 *
 */
public class ContentSwitchTest {
    private static final long COUNT = 10000000000L;

    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.fixedPool().setPoolSize(1).build();

    public static void main(String[] args){
        concurrency();
        serial();
    }


    private static void concurrency(){
        long start = System.currentTimeMillis();
        threadPoolExecutor.execute(() -> {
            int a =0;
            for (long i =0;i < COUNT;i++){
                a+=5;
            }
            System.out.println(a);
        });
        int b = 0;
        for(long i = 0;i < COUNT;i ++){
            b--;
        }
        threadPoolExecutor.shutdown();
        long time = System.currentTimeMillis() - start;
        System.out.println("concurrency :"  +time +"ms,b="+b);

    }

    private static void serial(){
        long start = System.currentTimeMillis();
        int a = 0;
        for(long i = 0;i < COUNT;i++){
            a+=5;
        }
        int b = 0;
        for(long i = 0 ;i <COUNT;i++){
            b--;
        }
        long time = System.currentTimeMillis()-start;
        System.out.println("serial:"+time+"ms,b="+b+",a="+a);
    }
}

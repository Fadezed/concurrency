package com.example.concurrency.contentSwitch;

/**
 * @author zed
 * 上下文切换测试类
 * 1、结论 循环数量在百万级别时 并发处理速度才会领先，说明上下文切换的存在
 * 2、使用vmstat 查看测量上下文切换次数得出 1000次/s
 * 3、减少上下文切换
 *  1）无锁并发编程-》数据ID按照Hash算法取模分段 不同线程处理不同数据
 *  2）CAS算法
 *  3）协程：在单线程里实现多任务调度，并在单线程里维持多个任务的切换
 *
 */
public class ContentSwitchTest {
    private static final long COUNT = 10000000L;
    public static void main(String[] args) throws InterruptedException{
        concurrency();
        serial();
    }


    private static void concurrency() throws InterruptedException{
        long start = System.currentTimeMillis();
        Thread thread = new Thread(() -> {
            int a =0;
            for (long i =0;i < COUNT;i++){
                a+=5;
            }
        });
        thread.start();
        int b = 0;
        for(long i = 0;i < COUNT;i ++){
            b--;
        }
        thread.join();
        long time = System.currentTimeMillis() - start;
        System.out.println("concurrency :"  +time +"ms,b="+b);

    }

    private static void serial() throws InterruptedException{
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

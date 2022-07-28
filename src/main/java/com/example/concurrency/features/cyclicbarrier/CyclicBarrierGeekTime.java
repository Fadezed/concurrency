package com.example.concurrency.features.cyclicbarrier;

import com.example.concurrency.features.threadPool.ThreadPoolBuilder;
import java.util.concurrent.*;

public class CyclicBarrierGeekTime {
    //订单队列
    public static ArrayBlockingQueue<String> pos = new ArrayBlockingQueue<String>(10);
    //物流队列
    public static ArrayBlockingQueue<String> dos  = new ArrayBlockingQueue<String>(10);
    //这里线程设为1，防止多线程并发导致的数据不一致，因为订单和物流是两个队列
    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.fixedPool().setPoolSize(1).setThreadNamePrefix("测试线程同步").build();
    public static CyclicBarrier cyclicBarrier = new CyclicBarrier(2,()->{
        //这一步用个线程池是因为要进行异步操作
        threadPoolExecutor.execute(CyclicBarrierGeekTime::check);
    });
    static void check(){
        System.out.println("对账结果"+ (pos.poll() + "\t" + dos.poll()) +","+Thread.currentThread().getName());
    }

    public static void main(String[] args) throws InterruptedException {
        //循环查询订单
        Thread t1 = new Thread(()->{
            for(int i = 0;i<4;i++){
                System.out.println("订单ok,"+Thread.currentThread().getName());
                pos.offer("订单"+i);
                try {
                    //仅仅方便观察输出
                    Thread.sleep(1000);
                    //所有阻塞式操作均用超时的方式调用
                    cyclicBarrier.await(1,TimeUnit.SECONDS);
                } catch (Exception e) {
                    System.out.println("等超时了"+e.getMessage());
                }
            }

        });
        //循环查询物流库
        Thread t2 = new Thread(()->{
            //这里设置比订单的循环少，以让订单的cyclicBarrier超时
            for(int j=0;j<2;j++){
                System.out.println("物流ok,"+Thread.currentThread().getName());
                dos.offer("物流"+j);
                try {
                    Thread.sleep(1000);
                    cyclicBarrier.await(1,TimeUnit.SECONDS);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

        });
        t1.start();
        t2.start();
        Thread.sleep(20000);
        threadPoolExecutor.shutdown();
    }
}

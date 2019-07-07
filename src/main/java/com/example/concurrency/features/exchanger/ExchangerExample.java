package com.example.concurrency.features.exchanger;

import com.example.concurrency.features.threadPool.ThreadPoolBuilder;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述:
 * 交换者 用于两个线程间的数据交换
 *
 * @author zed
 * @since 2019-07-03 5:39 PM
 */
public class ExchangerExample {
    private static final Exchanger<String> EXCHANGER = new Exchanger<>();
    private static ThreadPoolExecutor poolExecutor = ThreadPoolBuilder.fixedPool().setPoolSize(2).build();

    public static void main(String[] args) {
        poolExecutor.execute(()->{
            try{
                String s ="SomethingAndA";
                EXCHANGER.exchange(s);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });
        poolExecutor.execute(()->{
            try{
                String s1 = "SomethingAndB";
                String s = EXCHANGER.exchange("s1");
                System.out.println("s和s1值是否相等："+s1.equals(s)+",s："+s+",s1:"+s1);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });
        poolExecutor.shutdown();
    }
}


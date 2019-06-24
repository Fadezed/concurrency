package com.example.concurrency.visibility;

import com.example.concurrency.threadPool.ThreadPoolBuilder;
import com.example.concurrency.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述:
 * visibility 可见性问题
 *
 * @author zed
 * @since 2019-06-13 9:05 AM
 */
@Slf4j
public class Visibility {
    private static long count = 0;
    private void add10K() {
        int idx = 0;
        while(idx++ < 10000) {
            count += 1;
        }
    }

    /**
     * 通过boolean 变量更加直观
     */
    private static boolean flag = true;
    public static void main(String[] args) {
//        System.out.println(calc());

        log.info("我开始了");
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.fixedPool().build();
        //线程开始
        threadPoolExecutor.execute(() -> {
            while(flag){

            }
            log.info("我退出了");

        });
        ThreadUtil.sleep(100);
        flag = false;
    }
    private static long calc(){
        final Visibility visibility = new Visibility();
        // 创建两个线程，执行 add() 操作
        Thread th1 = new Thread(()->{
            visibility.add10K();
        });
        Thread th2 = new Thread(()->{
            visibility.add10K();
        });
        // 启动两个线程
        th1.start();
        th2.start();
        // 等待两个线程执行结束
        try{
            th1.join();
            th2.join();
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
        return count;

    }

}


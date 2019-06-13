package com.example.concurrency.synchronizedEx;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * Synchronized
 *
 * @author zed
 * @since 2019-06-13 11:47 AM
 */
public class SynchronizedExample {

    /**
     * 利用Synchronized 实现原子操作
     */
    static class SafeCalc {
        long value = 0L;
        synchronized long get() {
            return value;
        }
        synchronized void addOne() {
            value += 1;
        }
    }

    public static void main(String[] args) {
        SafeCalc safeCalc = new SafeCalc();
        List<Thread> ts = new ArrayList<>(100);
        for (int j = 0; j < 100;j++){
            Thread t = new Thread(() -> {
                for(int i = 0;i < 10; i++){
                    safeCalc.addOne();
                }
            });
            ts.add(t);
        }
        for(Thread t :ts){
            t.start();
        }
        //等待所有线程执行完成
        for(Thread t:ts){
            try{
                t.join();
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        System.out.println(safeCalc.get());

    }

}


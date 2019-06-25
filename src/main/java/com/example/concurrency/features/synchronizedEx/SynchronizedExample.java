package com.example.concurrency.features.synchronizedEx;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * Synchronized 实现原理 基于操作系统Mutex Lock (互斥锁)实现，所以每次获取和释放都会由用户态和内核态的切换成本高，jdk1.5之前性能差
 * JVM通过ACC_SYNCHRONIZED 标识一个方法是否为同步方法,而代码块则通过monitorenter和monitorexit指令操作monitor对象
 *
 *
 *
 ** @author zed
 * @since 2019-06-13 11:47 AM
 */
public class SynchronizedExample {

    static class X {
        /**
         * 修饰非静态方法 锁对象为当前类的实例对象 this
         */
        synchronized void get() {
        }

        /**
         * 修饰静态方法 锁对象为当前类的Class对象 Class X
         */
        synchronized static void set() {
        }

        /**
         * 修饰代码块
         */
        Object obj = new Object();
        void put() {
            synchronized(obj) {
            }
        }
    }

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


package com.example.concurrency.features.volatilecase;

import java.util.Scanner;

/**
 * 描述:
 * volatileEx
 *
 * Happens-Before
 * jdk 1.5 增强volatile 语义，添加内存屏障保证顺序性
 *
 * 多线程之间线程数据共享测试
 *
 * volatile 只能保证可见性 无法保证原子性
 *
 * @author zed
 */
public class VolatileExample {
    private static int x = 0;
    private static volatile boolean flag = false;

    private static void writer(){
        x = 123;
        flag = true;
    }
    private static void reader(){
        if(flag){
            System.out.println(x);
        }
    }

    public static void main(String[] args) {
        writer();
        reader();
        Volatile aVolatile = new Volatile();
        new Thread(aVolatile,"thread Test").start();

        System.out.println(Thread.currentThread().getName()+"正在执行") ;
        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()){
            String value = sc.next();
            if("1".equals(value)){
                new Thread(aVolatile::stopThread).start();
                break ;
            }
        }
        System.out.println(Thread.currentThread().getName()+"退出了！");
    }
    static class  Volatile implements Runnable{
        private volatile boolean flag = true ;

        @Override
        public void run() {
            //通过scanner 输入跳出
            while (flag){
            }
            System.out.println(Thread.currentThread().getName() +"执行完毕");
        }

        private void stopThread(){
            flag = false ;
        }
    }

    /**
     * 使用 volatile 修饰基本数据内存不能保证原子性
     * @link com.example.concurrency.features.atomic.AtomicCounter
     */
    static class VolatileInc implements Runnable{

        private static volatile int count = 0 ;

        //private static AtomicInteger count = new AtomicInteger() ;

        @Override
        public void run() {
            for (int i=0;i<10000 ;i++){
                count ++ ;
                //count.incrementAndGet() ;
            }
        }

        public static void main(String[] args) throws InterruptedException {
            VolatileInc volatileInc = new VolatileInc() ;
            Thread t1 = new Thread(volatileInc,"t1") ;
            Thread t2 = new Thread(volatileInc,"t2") ;
            t1.start();
            t2.start();

            t1.join();


            t2.join();
//            for (int i=0;i<10000 ;i++){
//                count ++ ;
//                //count.incrementAndGet();
//            }


            System.out.println("最终Count="+count);
        }
    }

}


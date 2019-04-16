package com.example.concurrency.deadLock;

/**
 * 死锁测试
 * 避免死锁常用方法
 * 1、避免一个线程同时获取多个锁
 * 2、避免一个线程在锁内同时占用多个资源，尽量保证每个锁只占用一个资源
 * 3、尝试使用定时锁，使用lock.tryLock(timeOut) 来替代使用内部锁机制
 * 4、对于数据库锁，加锁和解锁必须在一个数据库连接里，否则会出现解锁失败的情况
 *
 */
public class DeadLockTest {
    private static String A = "A";
    private static String B = "B";

    public static void main(String[] args) {
        new DeadLockTest().deadLock();
    }

    private void deadLock() {
        Thread t1 = new Thread(() -> {
            synchronized (A){
                try{
                    Thread.currentThread().sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                synchronized (B){
                    System.out.println("1");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (B) {
                synchronized (A) {
                    System.out.println("2");
                }
            }
        });
        t1.start();
        t2.start();
    }
}

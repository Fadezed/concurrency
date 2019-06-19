package com.example.concurrency.visibility;

/**
 * 描述:
 * visibility 可见性问题
 *
 * @author zed
 * @since 2019-06-13 9:05 AM
 */
public class Visibility {
    private static long count = 0;
    private void add10K() {
        int idx = 0;
        while(idx++ < 10000) {
            count += 1;
        }
    }

    public static void main(String[] args) {
        System.out.println(calc());
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


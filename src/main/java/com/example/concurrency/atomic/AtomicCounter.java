package com.example.concurrency.atomic;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * java实现原子操作
 * 补充CAS 实现原子操作的三大问题
 * 1、ABA问题 解决方式 加上版本号1A、2B、3A java 1.5 后提供AtomicStampedReference 检查当前引用是否符合预期引用，并检查当前标志是否等于预期标志
 * 2、循环时间长开销大
 * 3、只能保证一个共享变量的原子操作 取巧方法：多个共享变量合并成一个 AtomicReference 保证引用对象的原子性
 * @author zed
 */
public class AtomicCounter {
    private AtomicInteger atomicInteger = new AtomicInteger();
    private int i =0;
    public static void main(String[] args) {
        final AtomicCounter counter = new AtomicCounter();
        List<Thread> ts = new ArrayList<>(100);
        long start = System.currentTimeMillis();
        for (int j = 0; j < 100;j++){
            Thread t = new Thread(() -> {
                for(int i = 0;i < 100000; i++){
                    counter.count();
                    counter.safeCount();
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
                e.printStackTrace();
            }
        }
        System.out.println(counter.i);
        System.out.println(counter.atomicInteger.get());
        System.out.println(System.currentTimeMillis() - start);

    }

    /**
     * 使用CAS 实现线程安全计数器
     */
    private void safeCount() {
//        atomicInteger.incrementAndGet();
        for(;;){
            int i = atomicInteger.get();
            boolean flag = atomicInteger.compareAndSet(i,++i);
            if (flag){break;}
        }
    }

    /**
     * 非线程安全计数器
     */
    private void count() {
        i++;
    }
}

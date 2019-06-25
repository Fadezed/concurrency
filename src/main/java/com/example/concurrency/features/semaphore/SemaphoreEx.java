package com.example.concurrency.features.semaphore;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

/**
 * 描述:
 * Semaphore example
 *  acquire() 为原子操作 保证只有一个线程能将count减为0 则其他线程阻塞等待，release()+1后得以执行
 * @author zed
 * @since 2019-06-17 3:50 PM
 */
public class SemaphoreEx {
    /**
     * Semaphore 实现互斥
     */
    static class MutexSemaphore{
        private static int count;
        /**
         * 初始化信号量 permits 为1 表示只允许一个线程进入临界区
         */
        private static final Semaphore s = new Semaphore(1);

        /**
         * 用信号量保证互斥
         * @throws InterruptedException ex
         */
        static void addOne() throws InterruptedException{
            s.acquire();
            try {
                count+=1;
            } finally {
                s.release();
            }
        }
    }

    /**
     * 实现限流器
     * @param <T>
     * @param <R>
     */
    static class ObjPool<T, R> {
        final List<T> pool;
        /**
         * 用信号量实现限流器
         */
        final Semaphore semaphore;

        /**
         * 构造函数
         * @param size size
         * @param t t
         */
        ObjPool(int size, T t){
            pool = new Vector<T>(){};
            for(int i=0; i<size; i++){
                pool.add(t);
            }
            semaphore = new Semaphore(size);
        }

        /**
         * 利用对象池的对象，调用 func
         * @param func func
         * @return R
         * @throws InterruptedException ex
         */
        R exec(Function<T,R> func) throws InterruptedException{
            T t = null;
            semaphore.acquire();
            try {
                t = pool.remove(0);
                return func.apply(t);
            } finally {
                pool.add(t);
                semaphore.release();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException{
        // 创建对象池
        ObjPool<Object, String> pool = new ObjPool<>(10,"Worker");
        // 通过对象池获取 t，之后执行
        pool.exec(t -> {
            System.out.println(t);
            return t.toString();
        });
    }


}


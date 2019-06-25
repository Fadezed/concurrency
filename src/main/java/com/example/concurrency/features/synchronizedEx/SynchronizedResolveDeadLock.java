package com.example.concurrency.features.synchronizedEx;


import com.example.concurrency.features.threadPool.ThreadPoolBuilder;
import com.example.concurrency.util.ThreadDumpHelper;
import com.example.concurrency.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述:
 * 解决死锁问题
 *
 * @author zed
 * @since 2019-06-13 3:35 PM
 */
public class SynchronizedResolveDeadLock {

    class Account {
        /**
         * allocator 应该为单例
         */
        private Allocator allocator;
        private int balance;
        void transfer(Account target, int amt){
            // 一次性申请转出账户和转入账户，直到成功
            //循环等待消耗CPU 故不是最好的方式
            while(!allocator.apply(this, target)){

            }
            try{
                // 锁定转出账户
                synchronized(this){
                    // 锁定转入账户
                    synchronized(target){
                        if (this.balance > amt){
                            this.balance -= amt;
                            target.balance += amt;
                        }
                    }
                }
            } finally {
                allocator.free(this, target);
            }

        }
    }
    /**
     * 破坏占用且等待条件
     * 加入分配类，只有在同时拿到两个锁的时候才可以执行后续操作
     */
    class Allocator {
        private List<Object> als = new ArrayList<>();
        /**
         * 一次性申请所有资源
         * @param from 转入账户
         * @param to 转出账户
         * @return boolean
         */
        synchronized boolean apply(Object from, Object to){
            if(als.contains(from) || als.contains(to)){
                return false;
            } else {
                als.add(from);
                als.add(to);
            }
            return true;
        }

        /**
         * 归还资源
         * @param from 转入账户
         * @param to 转出账户
         */
        synchronized void free(Object from, Object to){
            als.remove(from);
            als.remove(to);
        }
    }
    class WaitAndNotifyAccount {
        /**
         * allocator 应该为单例
         */
        private Allocator allocator;
        private int balance;
        void transfer(WaitAndNotifyAccount target, int amt){
            WaitAndNotifyAllocator.getInstance().apply(this,target);
            this.balance -= amt;
            target.balance+=amt;
            WaitAndNotifyAllocator.getInstance().free(this,target);
        }
    }
    /**
     * 利用等待通知方式实现
     */
    static class WaitAndNotifyAllocator {
        private WaitAndNotifyAllocator(){}
        private List<Object> als;
        synchronized void apply(Object from, Object to){
            while(als.contains(from) || als.contains(to)){
                try{
                    wait();
                }catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
            als.add(from);
            als.add(to);
        }
        synchronized void free(
                Object from, Object to){
            als.remove(from);
            als.remove(to);
            notifyAll();
        }

        /**
         * WaitAndNotifyAllocator 为单例
         * @return WaitAndNotifyAllocator
         */
        public static WaitAndNotifyAllocator getInstance(){
            return AllocatorSingle.instance;
        }
        static class AllocatorSingle{
            static WaitAndNotifyAllocator instance = new WaitAndNotifyAllocator();
        }
    }


    /**
     * 根据账户id 排序保证获取锁顺序避免出现竞争 成本相对上面较低
     *
     */
    static class SortExeAccount {
        private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.fixedPool().setPoolSize(2).setThreadNamePrefix("SortExeAccount").build();
        private static ThreadDumpHelper dumpHelper = new ThreadDumpHelper();

        private SortExeAccount(int id, int balance) {
            this.id = id;
            this.balance = balance;
        }

        private int id;
        private int balance;
        void transfer(SortExeAccount target, int amt){
            SortExeAccount left = this;
            SortExeAccount right = target;
            if (this.id > target.id) {
                left = target;
                right = this;
            }
            // 锁定序号小的账户
            synchronized(left){
                ThreadUtil.sleep(2);

                // 锁定序号大的账户
                synchronized(right){
                    if (this.balance > amt){
                        this.balance -= amt;
                        target.balance += amt;
                    }
                }
            }
        }
        public static void main(String[] args) {
            SortExeAccount a = new SortExeAccount(1,200);
            SortExeAccount b = new SortExeAccount(2,200);
            threadPoolExecutor.execute(() -> {
                a.transfer(b,100);
            });
            threadPoolExecutor.execute(() -> {
                b.transfer(a,100);
            });
            threadPoolExecutor.shutdown();
            System.out.println(a.balance);
            System.out.println(b.balance);
            dumpHelper.tryThreadDump();
        }
    }


}


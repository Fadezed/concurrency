package com.example.concurrency.synchronizedEx;

import com.example.concurrency.threadPool.ThreadPoolBuilder;
import com.example.concurrency.util.ThreadDumpHelper;
import com.example.concurrency.util.ThreadUtil;

import java.util.Scanner;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述:
 * 保护有关联关系的多个资源 银行转帐问题
 *
 * @author zed
 * @since 2019-06-13 1:13 PM
 */
public class SynchronizedConnection {
    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.fixedPool().setPoolSize(2).setThreadNamePrefix("sync测试线程").build();

    /**
     * 线程不安全
     */
    static class UnsafeAccount {
        private int balance;
        private UnsafeAccount(int balance){
            this.balance =balance;
        }

        /**
         * 转账
         * @param target 目标账户
         * @param amt 转账数量
         */
        void transfer(
                UnsafeAccount target, int amt){
            if (this.balance > amt) {
                this.balance -= amt;
                target.balance += amt;
            }
        }
    }
    /**
     * 直接对转账方法做同步方式
     * 锁对象为this 而target对象资源不受保护
     */
    static class SynchronizedTransferAccount {
        /**
         * 用于测试尽量保证并发
         */
        private volatile boolean flag = true;
        private void breakLoop(){
            this.flag =false;
        }
        private int balance;

        private SynchronizedTransferAccount(int balance){
            this.balance =balance;
        }
        /**
         * 同步转账
         * @param target 目标账户
         * @param amt 转账数量
         */
        synchronized void transfer(SynchronizedTransferAccount target, int amt){

            if (this.balance >= amt) {
                while (flag){

                }
                System.out.println(Thread.currentThread().getName()+"this amount"+this.balance+"target amount:"+target.balance);
                this.balance -= amt;
                target.balance += amt;
                System.out.println(Thread.currentThread().getName()+"this amount"+this.balance+"target amount:"+target.balance);
            }

        }
        public static void main(String[] args){
            SynchronizedTransferAccount a = new SynchronizedTransferAccount(200);
            SynchronizedTransferAccount b = new SynchronizedTransferAccount(200);
            SynchronizedTransferAccount c = new SynchronizedTransferAccount(200);

            //账户a给b转账100
            threadPoolExecutor.execute(() -> {
                a.transfer(b,100);
            });
            //账户b转账给c 100
            threadPoolExecutor.execute(() -> {
                b.transfer(c,100);
            });

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String v = scanner.next();
                if("1".equals(v)){
                    a.breakLoop();
                    b.breakLoop();
                    c.breakLoop();
                    break;
                }
            }
            threadPoolExecutor.shutdown();
            System.out.println("a:"+a.balance);
            System.out.println("b:"+b.balance);
            System.out.println("c:"+c.balance);
            // 线程t1 和t2 如果同时进入临界区则 读取到的账户b的余额都是200 因此结果会因为t1 和t2的执行顺序变成100 或者300
        }
    }
    /**
     * 通过传入锁的方式 但是必须保持lock对象跟SafeAccount 一对一绑定
     * 并且真实场景中创建账户对象的地方可能是分布式，所以会很复杂极不推荐！
     */
    static class SafeAccount {
        private Object lock;
        private int balance;
        private SafeAccount(){}
        /**
         * 创建 SafeAccount 时传入同一个 lock 对象
         * @param lock lock
         */
        public SafeAccount(Object lock) {
            this.lock = lock;
        }
        void transfer(SafeAccount target, int amt){
            // 此处检查所有对象共享的锁
            synchronized(lock) {
                if (this.balance > amt) {
                    this.balance -= amt;
                    target.balance += amt;
                }
            }
        }
    }

    /**
     * 通过锁类对象方式
     * 同样能解决并发问题 但是会导致操作串行 性能极差故不推荐
     */
    class Account {
        private int balance;
        void transfer(Account target, int amt){
            synchronized(Account.class) {
                if (this.balance > amt) {
                    this.balance -= amt;
                    target.balance += amt;
                }
            }
        }
    }

    /**
     * 分别加锁方式 支持并行 锁粒度变细性能提升
     *
     * 但是 会导致严重问题： 死锁！！
     */
    static class DeadLockAccount {
        private static ThreadDumpHelper dumpHelper = new ThreadDumpHelper();
        private int balance;
        void transfer(DeadLockAccount target, int amt){
            // 锁定转出账户
            synchronized(this) {
                /*
                 * A、B两个账户
                 * 线程T1 A转给B
                 * 线程T2 B转给A
                 * T1 拿到A的锁同时T2拿到B的锁
                 * T1等待B 的锁
                 * T2等待A 的锁
                 *
                 */
                //线程sleep用于测试
                ThreadUtil.sleep(2);
                // 锁定转入账户
                synchronized(target) {
                    if (this.balance > amt) {
                        this.balance -= amt;
                        target.balance += amt;
                    }
                }
            }
        }

        public static void main(String[] args) {
            DeadLockAccount a = new DeadLockAccount();
            DeadLockAccount b = new DeadLockAccount();
            threadPoolExecutor.execute(() -> {
                a.transfer(b,100);
            });

            threadPoolExecutor.execute(() -> {
                b.transfer(a,100);
            });
            threadPoolExecutor.shutdown();
            dumpHelper.tryThreadDump();
        }
    }

}


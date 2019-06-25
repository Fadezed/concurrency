package com.example.concurrency.features.singleton;

/**
 * 描述:
 * 双重检查锁实现单例
 *
 * @author zed
 */
public class Singleton {
    /**
     * volatile保证可见性
     */
    private static volatile Singleton singleton;

    /**
     * 私有无参构造保证唯一入口
     */
    private Singleton() {
    }

    public static Singleton getInstance() {
        if (singleton == null) {
            synchronized (Singleton.class) {
                if (singleton == null) {
                    //防止指令重排
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
}


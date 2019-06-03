package com.example.concurrency.util;

import java.util.concurrent.TimeUnit;

/**
 * 描述:
 * ThreadUtil
 *  1. 处理了InterruptedException的sleep
 *
 *  2. 正确的InterruptedException处理方法
 *
 * @author zed
 * @since 2019-06-03 11:22 AM
 */
public class ThreadUtil {
    /**
     * sleep等待, 单位为毫秒, 已捕捉并处理InterruptedException.
     * @param durationMillis durationMillis
     */
    public static void sleep(long durationMillis) {
        try {
            Thread.sleep(durationMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * sleep等待，已捕捉并处理InterruptedException.
     * @param durationMillis value
     * @param unit unit
     */
    public static void sleep(long durationMillis, TimeUnit unit) {
        try {
            Thread.sleep(unit.toMillis(durationMillis));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 纯粹为了提醒下处理InterruptedException的正确方式，除非你是在写不可中断的任务.
     */
    public static void handleInterruptedException() {
        Thread.currentThread().interrupt();
    }
}


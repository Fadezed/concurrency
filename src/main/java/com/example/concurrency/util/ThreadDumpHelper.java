package com.example.concurrency.util;

import com.example.concurrency.limiter.TimeIntervalLimiter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 * 由程序触发的ThreadDump，打印到日志中
 * 因为ThreadDump本身会造成JVM停顿，所以加上了开关和最少间隔时间的选项(默认不限制)
 * 因为ThreadInfo的toString()最多只会打印8层的StackTrace，所以加上了最大打印层数的选项(默认为8)
 *
 * @author zed
 */
@Slf4j
public class ThreadDumpHelper {
    private static final int DEFAULT_MAX_STACK_LEVEL = 8;
    /**
     * 10分钟
     */
    private static final int DEFAULT_MIN_INTERVAL = 1000 * 60 * 10;

    /**
     * 打印StackTrace的最大深度
     */
    private int maxStackLevel;

    private TimeIntervalLimiter timeIntervalLimiter;

    public ThreadDumpHelper() {
        this(DEFAULT_MIN_INTERVAL, DEFAULT_MAX_STACK_LEVEL);
    }

    public ThreadDumpHelper(long leastIntervalMills, int maxStackLevel) {
        this.maxStackLevel = maxStackLevel;
        timeIntervalLimiter = new TimeIntervalLimiter(leastIntervalMills, TimeUnit.MILLISECONDS);
    }

    /**
     * 符合条件则打印线程栈
     */
    public void tryThreadDump() {
        tryThreadDump(null);
    }

    /**
     * 符合条件则打印线程栈
     *
     * @param reasonMsg 发生ThreadDump的原因
     */
    public void tryThreadDump(String reasonMsg) {
        if (timeIntervalLimiter.tryAcquire()) {
            threadDump(reasonMsg);
        }
    }

    /**
     * 强行打印ThreadDump，使用最轻量的采集方式，不打印锁信息
     * @param reasonMsg msg
     */
    private void threadDump(String reasonMsg) {
        log.info("Thread dump by ThreadDumpper" + (reasonMsg != null ? (" for " + reasonMsg) : ""));

        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        // 两条日志间的时间间隔，是VM被thread dump堵塞的时间.
        log.info("Finish the threads snapshot");

        StringBuilder sb = new StringBuilder(8192 * 20).append('\n');

        for (Map.Entry<Thread, StackTraceElement[]> entry : threads.entrySet()) {
            dumpThreadInfo(entry.getKey(), entry.getValue(), sb);
        }
        log.info(sb.toString());
    }

    /**
     * 打印全部的stack，重新实现threadInfo的toString()函数，因为默认最多只打印8层的stack. 同时，不再打印lockedMonitors和lockedSynchronizers
     * @param thread t
     * @param stackTrace s
     * @param sb sb
     */
    private void dumpThreadInfo(Thread thread, StackTraceElement[] stackTrace, StringBuilder sb) {
        sb.append('\"').append(thread.getName()).append("\" Id=").append(thread.getId()).append(' ')
                .append(thread.getState());
        sb.append('\n');
        int i = 0;
        for (; i < Math.min(maxStackLevel, stackTrace.length); i++) {
            StackTraceElement ste = stackTrace[i];
            sb.append("\tat ").append(ste).append('\n');
        }
        if (i < stackTrace.length) {
            sb.append("\t...").append('\n');
        }
        sb.append('\n');
    }

    /**
     * 打印ThreadDump的最小时间间隔，单位为秒，默认为0不限制
     * @param leastIntervalSeconds s
     */
    public void setLeastInterval(int leastIntervalSeconds) {
        this.timeIntervalLimiter = new TimeIntervalLimiter(leastIntervalSeconds, TimeUnit.MILLISECONDS);
    }

    /**
     * 打印StackTrace的最大深度, 默认为8
     * @param maxStackLevel level
     */
    public void setMaxStackLevel(int maxStackLevel) {
        this.maxStackLevel = maxStackLevel;
    }
}


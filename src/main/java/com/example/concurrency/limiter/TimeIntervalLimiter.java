package com.example.concurrency.limiter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 描述:
 * TimeIntervalLimiter
 *
 * @author zed
 * @since 2019-06-03 11:16 AM
 */
public class TimeIntervalLimiter {

    private final AtomicLong lastTimeAtom = new AtomicLong(0);

    private long windowSizeMillis;

    public TimeIntervalLimiter(long interval, TimeUnit timeUnit) {
        this.windowSizeMillis = timeUnit.toMillis(interval);
    }

    public boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        long lastTime = lastTimeAtom.get();
        return currentTime - lastTime >= windowSizeMillis && lastTimeAtom.compareAndSet(lastTime, currentTime);
    }
}


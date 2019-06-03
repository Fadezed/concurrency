package com.example.concurrency;

import com.example.concurrency.threadPool.ThreadPoolBuilder;
import com.example.concurrency.util.Concurrents;
import com.example.concurrency.util.ThreadUtil;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 描述:
 * ThreadDumpHelper Test
 *
 * @author zed
 * @since 2019-06-03 11:26 AM
 */
public class ThreadDumpHelperTest {
    public static class LongRunTask implements Runnable {

        private CountDownLatch countDownLatch;

        public LongRunTask(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            countDownLatch.countDown();
            ThreadUtil.sleep(5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void test() throws InterruptedException {
        ExecutorService executor = ThreadPoolBuilder.fixedPool().setPoolSize(10).build();
        CountDownLatch countDownLatch = Concurrents.countDownLatch(10);
        for (int i = 0; i < 10; i++) {
            executor.execute(new LongRunTask(countDownLatch));
        }
        countDownLatch.await();

        ThreadDumpHelper threadDumpHelper = new ThreadDumpHelper();
        threadDumpHelper.tryThreadDump();

        LogbackListAppender appender = new LogbackListAppender();
        appender.addToLogger(ThreadDumpHelper.class);

        // 设置最少间隔,不输出
        threadDumpHelper.setLeastInterval(1800);

        threadDumpHelper.tryThreadDump(); // 重置间隔会重置上一次写日志的时间,因此要调一次把新增的次数用完

        threadDumpHelper.tryThreadDump();
        assertThat(appender.getAllLogs()).hasSize(3);
        executor.shutdownNow();
    }
}


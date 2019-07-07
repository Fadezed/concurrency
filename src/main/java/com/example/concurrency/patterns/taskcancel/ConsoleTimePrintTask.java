package com.example.concurrency.patterns.taskcancel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述:
 * 控制台输出时间任务取消 通过interrupt() 注意抛出InterruptedException异常的地方需要重置下打断标识
 * 适用场景:当后台任务需要取消时
 * @author zed
 * @since 2019-06-30 3:18 PM
 */
public class ConsoleTimePrintTask {
    private Thread thread;
    /**
     * 执行线程
     */
    private Runnable task = () -> {
        while (!Thread.currentThread().isInterrupted()) {
            Date date = new Date(System.currentTimeMillis());
            System.out.println(new SimpleDateFormat().format(date));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // 注意这里如果没有抛出InterruptedException 则不需要执行interrupt
                Thread.currentThread().interrupt();
            }
        }
    };
    /**
     * run
     */
    public void run() {
        thread = new Thread(task);
        thread.start();
    }

    /**
     * cancel
     */
    public void cancel() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    public static void main(String[] args) {
        ConsoleTimePrintTask self = new ConsoleTimePrintTask();
        self.run();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        self.cancel();
    }
}


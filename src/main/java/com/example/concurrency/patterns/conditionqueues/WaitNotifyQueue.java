package com.example.concurrency.patterns.conditionqueues;

import com.example.concurrency.features.threadPool.ThreadPoolBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述:
 * 基于Object等待通知队列
 * wait() and notify
 *
 * @author zed
 * @since 2019-07-01 11:12 AM
 */
public class WaitNotifyQueue {
    private boolean continueToNotify;
    private BlockingQueue<String> messages;
    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.fixedPool().setPoolSize(2).setThreadNamePrefix("WaitNotifyQueue").build();


    private WaitNotifyQueue(List<String> messages) {
        this.messages = new LinkedBlockingQueue<>(messages);
        this.continueToNotify = true;
    }

    private synchronized void stopsMessaging() {
        continueToNotify = false;
        notifyAll();
    }

    private synchronized void message() throws InterruptedException {
        while (!continueToNotify){
            wait();
        }
        String message = messages.take();
        System.out.println(message);
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        List messages = new LinkedList<String>();
        for (int i = 0; i < 130; i++) {
            messages.add(UUID.randomUUID().toString());
        }
        WaitNotifyQueue waitNotifyQueue = new WaitNotifyQueue(messages);
        threadPoolExecutor.execute(() -> {
            try {
                while (true) {
                    waitNotifyQueue.message();
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        });

        Random random = new Random();
        threadPoolExecutor.execute(() -> {
            while (true) {
                int val = random.nextInt(100);
                System.out.println(val);
                if (val == 99) {
                    break;
                }
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
            waitNotifyQueue.stopsMessaging();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        });

    }
}


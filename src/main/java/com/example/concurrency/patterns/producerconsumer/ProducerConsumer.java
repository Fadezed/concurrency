package com.example.concurrency.patterns.producerconsumer;

import com.example.concurrency.features.threadPool.ThreadPoolBuilder;

import java.util.UUID;
import java.util.concurrent.*;

/**
 * 描述:
 * 生产消费
 * @author zed
 * @since 2019-07-01 2:12 PM
 */
public class ProducerConsumer {
    /**
     * 阻塞队列存储数据
     */
    private BlockingQueue<String> data = new LinkedBlockingQueue<>();
    /**
     * 消费者线程
     */
    private Callable<Void> consumer = () -> {
        while (true) {
            String dataUnit = data.poll(5, TimeUnit.SECONDS);
            if (dataUnit == null){
                break;
            }
            System.out.println("Consumed " + dataUnit + " from " + Thread.currentThread().getName());
        }
        return null;
    };
    /**
     * 生产者线程
     */
    private Callable<Void> producer = () -> {
        for (int i = 0; i < 90; i++) {
            String dataUnit = UUID.randomUUID().toString();
            data.put(dataUnit);
        }
        return null;
    };

    public static void main(String[] args) throws InterruptedException{
        ProducerConsumer producerConsumer = new ProducerConsumer();
        ThreadPoolExecutor pool = ThreadPoolBuilder.cachedPool().setThreadNamePrefix("生产消费线程").build();
        pool.submit(producerConsumer.producer);
        pool.submit(producerConsumer.consumer);
        pool.submit(producerConsumer.consumer);
        //非阻塞，不允许继续提交
        pool.shutdown();
        //阻塞，允许提交，返回终止结果 true/false
        pool.awaitTermination(5, TimeUnit.SECONDS);

    }
}


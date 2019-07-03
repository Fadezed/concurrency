package com.example.concurrency.features.completablefuture;

import com.example.concurrency.util.ThreadUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 * CompletableFutureExample  Java 8 异步编程可描述串行关系、AND汇聚关系和OR汇聚关系以及异常处理
 *
 * @author zed
 * @since 2019-07-03 1:57 PM
 */
public class CompletableFutureExample {
    /**
     * 任务 1：洗水壶 -> 烧开水
     */
    private CompletableFuture<Void> f1 = CompletableFuture.runAsync(()->{
                System.out.println("T1: 洗水壶...");
                ThreadUtil.sleep(1, TimeUnit.SECONDS);
                System.out.println("T1: 烧开水...");
                ThreadUtil.sleep(15, TimeUnit.SECONDS);
    });
    /**
     * 任务 2：洗茶壶 -> 洗茶杯 -> 拿茶叶
     */
    private CompletableFuture<String> f2 = CompletableFuture.supplyAsync(()->{
                System.out.println("T2: 洗茶壶...");
                ThreadUtil.sleep(1, TimeUnit.SECONDS);

                System.out.println("T2: 洗茶杯...");
                ThreadUtil.sleep(2, TimeUnit.SECONDS);

                System.out.println("T2: 拿茶叶...");
                ThreadUtil.sleep(1, TimeUnit.SECONDS);
                return " 龙井 ";
    });
    /**
     * 任务 3：任务 1 和任务 2 完成后执行：泡茶
     */
    private CompletableFuture<String> f3 = f1.thenCombine(f2, (e, tf)->{
                System.out.println("T1: 拿到茶叶:" + tf);
                System.out.println("T1: 泡茶...");
                return " 上茶:" + tf;
    });


    public static void main(String[] args) {
        CompletableFutureExample example = new CompletableFutureExample();
        System.out.println(example.f3.join());
    }

    /**
     * 描述串行关系 thenApply
     */
    static class SerialRelation{
        private static CompletableFuture<String> f0 =
                CompletableFuture.supplyAsync(
                        () -> "Hello World")      //①
                        .thenApply(s -> s + " QQ")  //②
                        .thenApply(String::toUpperCase);//③

        public static void main(String[] args) {
            System.out.println(SerialRelation.f0.join());
        }
    }
    /**
     * 描述汇聚Or关系 thenApply
     */
    static class ConvergeRelation{
        static CompletableFuture<String> f1 =
                CompletableFuture.supplyAsync(()->{
                    int t = getRandom(5, 10);
                    ThreadUtil.sleep(t, TimeUnit.SECONDS);
                    return String.valueOf(t);
                });

        static CompletableFuture<String> f2 =
                CompletableFuture.supplyAsync(()->{
                    int t = getRandom(5, 10);
                    ThreadUtil.sleep(t, TimeUnit.SECONDS);
                    return String.valueOf(t);
                });

        static CompletableFuture<String> f3 =
                f1.applyToEither(f2,s -> s);


        private static int getRandom(int i,int j){
            return (int) (Math.random() * (j - i)) +i;
        }
        public static void main(String[] args) {

            System.out.println(ConvergeRelation.f3.join());
        }
    }

    /**
     * 处理异常 此例为发生异常默认为0
     */
    static class ExceptionHandler{
        private static CompletableFuture<Integer>
                f0 = CompletableFuture
                .supplyAsync(()->7/0)
                .thenApply(r->r*10)
                .exceptionally(e->0);

        public static void main(String[] args) {
            System.out.println(ExceptionHandler.f0.join());
        }

    }

}


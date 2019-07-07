package com.example.concurrency.patterns.divideconquer;

import org.springframework.util.StopWatch;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * 描述:
 * 平行分治 fork/join
 *
 * @author zed
 * @since 2019-07-01 11:59 AM
 */
public class ParallelDivideAndConquer {
    /**
     * 分治执行
     */
    public static class ParallelSum extends RecursiveTask<BigInteger> {

        private static final long serialVersionUID = 1L;
        /**
         * 选择一个数来分割计算
         */
        private final static int THRESHOLD = 10_000;
        private List<BigInteger> bigIntegerList;

        public ParallelSum(List<BigInteger> bigIntegerList) {
            this.bigIntegerList = bigIntegerList;

        }
        @Override
        protected BigInteger compute() {
            int size = bigIntegerList.size();
            if (size < THRESHOLD) {
                return sequentialSum(bigIntegerList);
            } else {
                ParallelSum x = new ParallelSum(bigIntegerList.subList(0, size / 2));
                ParallelSum y = new ParallelSum(bigIntegerList.subList(size / 2, size));
                x.fork();
                y.fork();
                BigInteger xResult = x.join();
                BigInteger yResult = y.join();
                return yResult.add(xResult);
            }
        }
    }
    /**
     * 顺序执行
     * @param list list
     * @return sum
     */
    private static BigInteger sequentialSum(List<BigInteger> list) {
        BigInteger acc = BigInteger.ZERO;
        for (BigInteger value : list) {
            acc = acc.add(value);
        }
        return acc;
    }

    /**
     * 通过两种方式验证一千万个数据的累加执行速度
     * @param args args
     * @throws InterruptedException e
     */
    public static void main(String[] args) throws InterruptedException{
        List<BigInteger> list = LongStream.range(0, 10_000_000L)
                .mapToObj(BigInteger::valueOf)
                .collect(Collectors.toList());
        /*
         * Fork/Join 分值累加
         */
        Runnable parallel = () -> {
            ForkJoinPool commonPool = ForkJoinPool.commonPool();
            BigInteger result = commonPool.invoke(new ParallelSum(list));

            System.out.println("Parallel Result is: " + result);
        };
        /*
         * 串型累加
         */
        Runnable sequential = () -> {
            BigInteger acc = sequentialSum(list);

            System.out.println("Sequential Result is: " + acc);
        };

        System.out.println("first time 耗时 \n\n");
        dummyBenchmark(sequential);
        dummyBenchmark(parallel);

        Thread.sleep(2000);
        System.out.println("some JIT 之后\n\n");
        dummyBenchmark(sequential);
        dummyBenchmark(parallel);

        Thread.sleep(2000);
        System.out.println("more JIT之后 \n\n");
        dummyBenchmark(sequential);
        dummyBenchmark(parallel);


    }
    private static void dummyBenchmark(Runnable runnable) {
        StopWatch stopWatch = new StopWatch("耗时情况");
        stopWatch.start();
        runnable.run();
        stopWatch.stop();
        System.out.println("Executed in: " + stopWatch.prettyPrint());
        System.out.println("######\n");
    }
}


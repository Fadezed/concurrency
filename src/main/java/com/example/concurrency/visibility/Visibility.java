package com.example.concurrency.visibility;

import com.example.concurrency.threadPool.ThreadPoolBuilder;
import com.example.concurrency.util.ThreadUtil;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 描述:
 * visibility 可见性问题
 *
 * @author zed
 * @since 2019-06-13 9:05 AM
 */
public class Visibility {
    private static long count = 0;
    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolBuilder.fixedPool().build();

    private void add10k() {
        int idx = 0;
        while(idx++ < 10000) {
            count += 1;
        }
    }

    /**
     * 通过boolean 变量更加直观
     */
    private static boolean flag = true;
    public static void main(String[] args) {
//        System.out.println(calc());

        System.out.println("start");
        //线程开始
        threadPoolExecutor.execute(() -> {
            while(flag){

            }
            System.out.println("stop");

        });
        ThreadUtil.sleep(100);
        flag = false;
    }
    private static long calc(){
        final Visibility visibility = new Visibility();
        threadPoolExecutor.execute(visibility::add10k);
        threadPoolExecutor.execute(visibility::add10k);
        /*
         * 调用shuntDown保证线程执行完毕
         */
        threadPoolExecutor.shutdown();
        return count;

    }

}


package com.example.concurrency.finalEx;

/**
 * 描述: 构造函数的错误重排导致线程可能看到 final 变量的值会变
 *
 * FinalExample
 * @link http://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html
 * @author zed
 * @since 2019-06-13 1:38 PM
 */
public class FinalExample {
    private static class Global{
        private static FinalExample example;

    }
    private final int x;
    private int y;
    private static FinalExample f;
//    public FinalExample() {
//        x = 3;
//        y = 4;
//    }

    public FinalExample() { // bad!
        x = 3;
        y = 4;
     // bad construction - allowing this to escape
        Global.example = this;
    }

    private static void writer() {
        f = new FinalExample();
    }

    private static void reader() {
        if (f != null) {
            int i = f.x;
            int j = f.y;
        }
    }

    public static void main(String[] args) {
        writer();
        System.out.println(Global.example);
        reader();
    }
}


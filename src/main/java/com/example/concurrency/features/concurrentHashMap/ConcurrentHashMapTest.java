package com.example.concurrency.features.concurrentHashMap;

/**
 * 描述:
 * C map Test
 *
 * @author zed
 * @since 2019-06-04 2:30 PM
 */
public class ConcurrentHashMapTest {
    public static void main(String[] args) {
        Object o = new Object();
        try{
            synchronized (ConcurrentHashMapTest.class){
                o.wait(300000);
            }

        }catch (Exception e){
            System.out.println(e);
        }
        System.out.println(tableSizeFor(7));
        System.out.println(spread(1212321334));


        //(n - 1) & hash 确定数组位置
        System.out.println((16 - 1) & spread(1212321334));
        //等同于hash值对长度取模
        System.out.println(spread(1212321334)%16);
    }
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    /**
     * usable bits of normal node hash
     */
    private static final int HASH_BITS = 0x7fffffff;

    /**
     * 初始化容量大小 转换为2的幂次方
     * @param c size
     * @return int
     */
    private static int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /**
     * 将key的hashCode的低16位于高16位进行异或运算
     * re hash
     * @param h hashCode
     * @return int
     */
    private static int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }
}


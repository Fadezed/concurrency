package com.example.concurrency.features.atomic;

/**
 * 描述:
 * SimulatedCAS 模拟cas操作
 *
 * @author zed
 * @since 2019-06-18 2:09 PM
 */
public class SimulatedCAS{
    private int count;

    /**
     * 实现 count+=1
     * @param newValue new
     */
    void addOne(int newValue){
        //自旋
        do {
            newValue = count+1;
        }while(count != cas(count,newValue));
    }
    /**
     * 只有当目前 count 的值和期望值 expect 相等时，才会将 count 更新为 newValue。
     * @param expect 期望值
     * @param newValue 更新值
     * @return 旧值
     */
    private synchronized int cas(
            int expect, int newValue){
        // 读目前 count 的值
        int curValue = count;
        // 比较目前 count 值是否 == 期望值
        if(curValue == expect){
            // 如果是，则更新 count 的值
            count = newValue;
        }
        // 返回写入前的值
        return curValue;
    }
}


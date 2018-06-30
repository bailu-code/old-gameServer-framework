package com.ts.framework.helper;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 18位数值型唯一id生成器，起点时间1376150400s
 *
 * @author wl
 */
public class LongID {
    private static final long START_SEC = 1376150400L;// 起点时间
    private static final int MAX_SEC = 60 * 60 * 24 * 365 * 30;// 30年
    private static final long SEC_BIT = 1000000000;// 秒位数
    private static final int SUFFIX_BIT = 100000;// 后缀最大5位
    private AtomicLong currentId = new AtomicLong();//当前id
    private long currentMaxId;//当前id的最大值
    private long basePrefix;// 原始前缀
    private long recordTime;// 记录的时间点
    private AtomicBoolean getLock = new AtomicBoolean(false);//是否正在重置

    /**
     * 根据前缀初始化gid
     *
     * @param prefix 初始前缀
     */
    public static LongID valueOf(int... prefix) {
        if (prefix == null || prefix.length == 0) {
            throw new RuntimeException("prefix is null or size = 0");
        }
        LongID gid = new LongID();
        // 提前计算后缀最大值
        gid.basePrefix = prefix[0];
        for (int i = 1; i < prefix.length; i++) {
            int bit = String.valueOf(prefix[i]).length();
            for (int j = 0; j < bit; j++) {
                gid.basePrefix *= 10;
            }
            gid.basePrefix += prefix[i];
        }

        // 插入秒
        gid.basePrefix *= SEC_BIT;
        return gid;
    }

    /**
     * 获取一个唯一id
     */
    public long get() {
//        while (!getLock.compareAndSet(false, true)) {
//        }
//        long currentSec = System.currentTimeMillis() / 1000;
//        if (recordTime < currentSec) {//上一次获取id的时间，已经比当前时间晚，则重置id
//            recordTime = currentSec;
//            reset(recordTime);
//        }
//        long id = currentId.getAndIncrement();
//        if (id >= currentMaxId) {//当前秒已达到最大可获得id数
//            recordTime += 1;//提前获取下一秒
//            reset(recordTime);
//
//            id = currentId.getAndIncrement();
//        }
//        getLock.set(false);
//        return id;
        return currentId.getAndIncrement();
    }

    /**
     * 重置id生成器
     *
     * @param sec 所在秒
     */
    private void reset(long sec) {
        long offsetSec = sec - START_SEC;
        if (offsetSec >= MAX_SEC) {
            throw new RuntimeException("ID max now, please change START_SEC ");
        }
        currentId.set((basePrefix + offsetSec) * SUFFIX_BIT);
        currentMaxId = currentId.get() + SUFFIX_BIT - 1;
    }

}

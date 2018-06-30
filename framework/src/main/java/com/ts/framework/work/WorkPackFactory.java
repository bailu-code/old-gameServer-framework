package com.ts.framework.work;

import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * work包 生成器
 *
 * @author wl
 */
public interface WorkPackFactory {

    /**
     * 单个包的work容量
     */
    int onePackCapacity();

    /**
     * 获取一个空的workPack
     */
    WorkPack popPack(WorkQueue workQueue);

    /**
     * 归还使用结束的workPack
     */
    void returnPack(WorkPack workPack);

}

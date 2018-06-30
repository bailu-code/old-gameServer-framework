package com.ts.framework.work;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * work包 生成器
 *
 * @author wl
 */
public class DefaultWorkPackFactory implements WorkPackFactory {
    private static AtomicInteger PACK_INDEX = new AtomicInteger(1);
    private int holdCapacity = 10;
    private int packCapacity = 100;
    private Queue<WorkPack> list = new ConcurrentLinkedQueue<>();
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public int onePackCapacity() {
        return packCapacity;
    }

    /**
     * 获取一个空的workPack
     */
    @Override
    public WorkPack popPack(WorkQueue workQueue) {
        WorkPack workPack;
        while ((workPack = list.poll()) == null) {
            lock.lock();
            try {
                if (list.isEmpty()) {
                    for (int i = 0; i < holdCapacity; i++) {
                        list.add(new WorkPack(this, PACK_INDEX.getAndIncrement(), packCapacity));
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        workPack.setWorkQueue(workQueue);
        return workPack;
    }

    /**
     * 归还使用结束的workPack
     */
    @Override
    public void returnPack(WorkPack workPack) {
        workPack.reset();
        list.add(workPack);
    }

    public int getHoldCapacity() {
        return holdCapacity;
    }

    public void setHoldCapacity(int holdCapacity) {
        this.holdCapacity = holdCapacity;
    }

    public int getPackCapacity() {
        return packCapacity;
    }

    public void setPackCapacity(int packCapacity) {
        this.packCapacity = packCapacity;
    }
}

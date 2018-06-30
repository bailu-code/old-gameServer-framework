package com.ts.framework.work;

import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * work包
 *
 * @author wl
 */
class WorkPack {
    private WorkPackFactory factory;
    private String name;
    private WorkQueue workQueue;
    private int capacity;//容量
    private AtomicInteger addIndex;
    private int getIndex;
    private Work[] items;
    private boolean reading;//是否正在读

    public WorkPack(WorkPackFactory factory, int index, int capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.factory = factory;
        this.name = "WP"+index;
        this.capacity = capacity;
        this.items = new Work[capacity];
        this.addIndex = new AtomicInteger(0);
        this.getIndex = 0;
    }

    /**
     * 重置
     */
    @Scheduled()
    public void reset() {
        workQueue = null;
        addIndex.set(0);
        getIndex = 0;
        Arrays.fill(items, null);
        reading = false;
    }

    /**
     * 锁定读取
     */
    public void writeLock() {
        reading = true;
    }

    /**
     * 队列长度
     */
    public int size() {
        return addIndex.get();
    }

    /**
     * 是否拥有数据
     */
    public boolean isEmpty() {
        return addIndex.get() == 0;
    }

    /**
     * 是否已满
     */
    public boolean isFull(){
        return size() == capacity;
    }

    /**
     * 添加work
     */
    public boolean add(Work work) {
        if (work == null) {
            throw new NullPointerException();
        }
        if (reading) {
            return false;
        }
        int index = addIndex.get();
        if (index >= capacity) {
            return false;
        }
        if (!addIndex.compareAndSet(index, index + 1)) {
            return add(work);
        }
        items[index] = work;
        return true;
    }

    /**
     * 获取一个work，如果没有则返回null
     */
    public Work poll() {
        if (getIndex >= addIndex.get()) {
            return null;
        }
        return items[getIndex++];
    }

    /**
     * 业务处理完毕
     */
    public void readOver() {
        workQueue.runOver();
        factory.returnPack(this);
    }

    @Override
    public String toString() {
        return name +
                "[" +
                size() +
                ']';
    }

    public WorkQueue getWorkQueue() {
        return workQueue;
    }

    public void setWorkQueue(WorkQueue workQueue) {
        if (this.workQueue != null){
            throw new RuntimeException();
        }
        this.workQueue = workQueue;
    }

    public boolean isReading() {
        return reading;
    }
}

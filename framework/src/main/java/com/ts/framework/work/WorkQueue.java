package com.ts.framework.work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 业务队列
 *
 * @author wl
 */
public class WorkQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkQueue.class);
    private String queueName;
    private QueueWorkerGroup group;
    private WorkPackFactory factory;
    private WorkPack currentPack;//当前队列
    private Queue<WorkPack> waitPacks;
    private AtomicBoolean running;//队列是否正在运行
    private ReentrantLock lock;
    private Thread thread;
    private int num;

    WorkQueue(QueueWorkerGroup group, WorkPackFactory factory, String queueName, int capacity) {
        this.group = group;
        this.factory = factory;
        this.queueName = queueName;
        this.currentPack = factory.popPack(this);
        this.waitPacks = new ConcurrentLinkedQueue<>();
        this.running = new AtomicBoolean(false);
        this.lock = new ReentrantLock();
    }

    /**
     * 提交业务
     */
    public void submit(Work work) {
        lock.lock();
        try {
            if (work instanceof QueueWork){
                ((QueueWork)work).setCurrentQueue(this);
            }
            currentPack.add(work);
            trySubmitQueue();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 结束当前队列
     */
    public void runOver() {
        if (!running.compareAndSet(true, false)) {
            Thread.dumpStack();
            LOGGER.error("error queue running status, last Thread {}, current Thread {}", thread, Thread.currentThread());
        }

        trySubmitQueue();
    }

    /**
     * 尝试提交当前队列
     */
    private void trySubmitQueue() {
        lock.lock();
        try {
            if (currentPack.isFull()) {
                waitPacks.add(currentPack);
                currentPack = factory.popPack(this);
            }
            if (running.compareAndSet(false, true)) {
                if (!waitPacks.isEmpty()) {
                    group.submitQueue(waitPacks.poll());
                    return;//提交等待队列
                }
                if (!currentPack.isEmpty()) {
                    group.submitQueue(currentPack);
                    currentPack = factory.popPack(this);
                    return;//提交当前任务包
                }
                //提交失败
                running.set(false);
            }
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        int size = currentPack.size();
        for (WorkPack waitPack : waitPacks) {
            size += waitPack.size();
        }
        return size;
    }

    @Override
    public String toString() {
        return "Queue[" + queueName + "]";
    }

}

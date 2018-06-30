package com.ts.framework.work;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 队列工作者线程组，同一条队列只会被一条线程持有
 *
 * @author wl
 */
public class QueueWorkerGroup implements Thread.UncaughtExceptionHandler {
    private String name;
    private int queueCapacity;//队列容量
    private WorkPackFactory packFactory;
    private ArrayList<Worker> workers;//工作者线程集合
    private BlockingQueue<WorkPack> workPackQueues;//有任务的队列集合
    private BasicThreadFactory workerFactory;//worker工厂

    /**
     * @param groupName      组名
     * @param queueCapacity  队列容量
     * @param packFactory    业务包工厂
     * @param workerPoolSize 工作者线程数量
     */
    public QueueWorkerGroup(String groupName, int queueCapacity, int workerPoolSize, WorkPackFactory packFactory) throws IllegalAccessException {
        if (workerPoolSize <= 0) {
            throw new IllegalArgumentException("workerNum: " + workerPoolSize);
        }
        this.name = groupName;
        this.packFactory = packFactory;
        this.workPackQueues = new ArrayBlockingQueue<>(queueCapacity);

        BasicThreadFactory.Builder builder = new BasicThreadFactory.Builder();
        workerFactory = builder
                .namingPattern(groupName + "-%s")
                .uncaughtExceptionHandler(this)
                .build();

        this.workers = new ArrayList<>(workerPoolSize);
        for (int i = 0; i < workerPoolSize; i++) {
            this.workers.add(createWorker());
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

    }

    public Worker createWorker(){
        Worker worker = new Worker(this);
        Thread thread = workerFactory.newThread(worker);
        thread.start();
        return worker;
    }

    /**
     * 创建一条队列
     */
    public WorkQueue createQueue(String queueName) {
        return createQueue(queueName, Integer.MAX_VALUE);
    }

    /**
     * 创建一条队列
     *
     * @param capacity 队列容量，超过容量将会进行抛弃
     */
    public WorkQueue createQueue(String queueName, int capacity) {
        return new WorkQueue(this, packFactory, queueName, capacity);
    }

    /**
     * 提交任务包
     */
    public void submitQueue(WorkPack workPack) {
        if (workPack.isReading()){
            throw new RuntimeException();
        }
        workPack.writeLock();
        workPackQueues.add(workPack);
    }

    /**
     * 获取任务包
     */
    public WorkPack take() throws InterruptedException {
        return workPackQueues.take();
    }

    /**
     * 获取所有的工作者线程
     */
    public List<Worker> getAllWorkers() {
        return workers;
    }

    @Override
    public String toString() {
        return name;
    }
}

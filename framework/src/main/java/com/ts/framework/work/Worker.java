package com.ts.framework.work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工作者线程
 *
 * @author wl
 */
class Worker implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);
    private QueueWorkerGroup workerGroup;
    private Thread bindThread;//绑定线程
    private boolean run = true;
    private Work currentWork;//当前工作
    private long currentWorkStartTime;//当前业务开始时间
    private long lastWorkTime = System.currentTimeMillis();//最后一次工作的时间

    public Worker(QueueWorkerGroup workerGroup) {
        this.workerGroup = workerGroup;
    }

    /**
     * 关闭线程
     */
    public void shutdown() {
        this.run = false;
    }

    @Override
    public void run() {
        if (bindThread != null) {
            bindThread.interrupt();
            LOGGER.error("worker thread conflict, old {}, current {}", bindThread, Thread.currentThread());
        }
        bindThread = Thread.currentThread();
        while (run) {
            try {
                // 阻塞获取当前工作
                WorkPack workPack = workerGroup.take();
//                LOGGER.debug("{} run start {}", workPack.getWorkQueue(), workPack);
                while ((currentWork = workPack.poll()) != null) {
                    try {
                        currentWorkStartTime = System.currentTimeMillis();
//                        queue.workStart(currentWork);
                        currentWork.run();
//                        queue.workEnd(currentWork);
                        int useTime = (int) (System.currentTimeMillis() - currentWorkStartTime);
//                        Watcher.INSTANCE.workEnd(this, currentWork, useTime);

                    } catch (Exception e) {
                        LOGGER.error(this + " run error", e);
//                        Watcher.INSTANCE.workException(this, currentWork, e);
                    } finally {
                        // 重置
                        currentWork = null;
                        currentWorkStartTime = 0;
                        lastWorkTime = System.currentTimeMillis();
                    }
                }
//                LOGGER.debug("{} run over {}", workPack.getWorkQueue(), workPack);
                workPack.readOver();
            } catch (InterruptedException e) {
                LOGGER.warn("{} interrupted", this);
                return;
            }
        }

        LOGGER.warn("worker {} shutdown", this);
    }

    /**
     * 是否在工作中
     */
    public boolean isWorking() {
        return currentWork != null;
    }

    /**
     * @return 当前业务开始运行时间，如果当前没有业务在运行，则为0
     */
    public long getCurrentRunTime() {
        if (currentWorkStartTime == 0) {
            return 0;
        }
        long now = System.currentTimeMillis();
        long useTime = now - currentWorkStartTime;
        return useTime == now ? 0 : useTime;
    }

    public Work getCurrentWork() {
        return currentWork;
    }

    public long lastWorkTime() {
        return lastWorkTime;
    }

    public long freeTime() {
        return System.currentTimeMillis() - lastWorkTime;
    }

    public Thread getBindThread() {
        return bindThread;
    }

    @Override
    public String toString() {
        return bindThread.toString();
    }
}

	
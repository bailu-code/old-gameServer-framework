package com.ts.framework.work;

/**
 * 抽象work基类，实现work队列跳转功能
 *
 * @author wl
 */
public abstract class QueueWork implements Work {
    private WorkQueue currentQueue;// 当前队列
    private int currentStep = 0;// 下一次执行的位置

    /**
     * 默认执行队列
     */
    public abstract WorkQueue defaultQueue();

    /**
     * 消息执行
     *
     * @param step 当前步数，默认从0开始
     */
    protected abstract void run(int step);

    /**
     * 提交自己到队列中
     */
    public void submit(){
        defaultQueue().submit(this);
    }

    /**
     * 执行队列跳转，默认下一步数为当前步数+1
     *
     * @param nextQueue 跳转到的队列
     */
    protected final void jumpQueue(WorkQueue nextQueue) {
        jumpQueue(nextQueue, ++currentStep);
    }

    /**
     * 重新提交该work到队列中，默认下一步数为当前步数+1
     */
    protected final void asyncNext() {
        jumpQueue(this.currentQueue);
    }

    /**
     * 执行队列跳转
     *
     * @param nextQueue 跳转到的队列
     * @param nextStep  跳转到的步数
     */
    protected final void jumpQueue(WorkQueue nextQueue, int nextStep) {
        this.currentStep = nextStep;
        nextQueue.submit(this);
    }

    @Override
    public void run() {
        run(currentStep);
    }

    public WorkQueue getCurrentQueue() {
        return currentQueue;
    }

    public void setCurrentQueue(WorkQueue currentQueue) {
        this.currentQueue = currentQueue;
    }


}

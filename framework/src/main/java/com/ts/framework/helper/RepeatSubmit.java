package com.ts.framework.helper;

/**
 * 重复提交记录
 * @author wl
 */
public class RepeatSubmit {
    private volatile long submitTime;//提交时间
    private volatile long runTime;//实际执行时间

    /**
     * 是否在提交后已执行，如果已执行过，返回true，如果未执行过，则返回false，并且将执行时间设置为当前时间
     */
    public boolean isRepeat() {
        if (runTime > submitTime) {
            return true;
        } else {
            runTime = System.currentTimeMillis();
            return false;
        }
    }

    /**
     * 进行提交动作，将提交时间设置为当前时间
     */
    public void submit() {
        submit(System.currentTimeMillis());
    }

    /**
     * 进行提交动作，将提交时间设置为指定时间
     */
    public void submit(long time) {
        this.submitTime = time;
    }

    /**
     * 最后一次存档时间
     */
    public long lastRuntime() {
        return runTime;
    }

}

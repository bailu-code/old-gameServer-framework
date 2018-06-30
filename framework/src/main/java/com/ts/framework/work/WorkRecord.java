package com.ts.framework.work;

/**
 * work执行记录
 * @author wl
 */
class WorkRecord implements Comparable<WorkRecord> {
    private String className;//业务名
    private long runCount;//执行次数
    private long totalUseTime;//总耗时时间
    private int exceptionCount;//异常次数
    private int timeoutCount;//超时次数

    public WorkRecord(String className) {
        this.className = className;
    }

    /**
     * 业务执行结束
     */
    void runEnd(int useTime) {
        runCount++;
        totalUseTime += useTime;
    }

    /**
     * 业务运行出现异常
     */
    void exception(Exception e) {
        exceptionCount++;
    }

    /**
     * 业务超时
     */
    void timeout(){
        timeoutCount++;
    }

    @Override
    public int compareTo(WorkRecord o) {
        if (o.avgTime() > avgTime()) {
            return 1;
        } else if (o.avgTime() == avgTime()) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * @return 平均时间ms
     */
    public float avgTime() {
        return (float) (totalUseTime / runCount);
    }

    /**
     * @return 业务名称
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return 异常产生次数
     */
    public int getExceptionCount() {
        return exceptionCount;
    }

    /**
     * @return 超时次数
     */
    public int getTimeoutCount() {
        return timeoutCount;
    }
}
package com.ts.framework.work;

/**
 * 工作接口
 *
 * @author wl
 */
public interface Work extends Runnable {

    /**
     * 初始化运行数据
     */
    void init(Args args);

    /**
     * 执行
     */
    void run();

}

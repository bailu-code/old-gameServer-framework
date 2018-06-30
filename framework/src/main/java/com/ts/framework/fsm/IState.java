package com.ts.framework.fsm;

/**
 * 状态处理接口
 * @author wl
 */
public interface IState<Type, T> {

    /**
     * 状态类型
     */
    Type stageType();

    /**
     * 进入该阶段
     *
     * @param ringState 环形状态机
     * @param t         状态机处理对象
     * @param time      当前时间
     */
    void in(RingState<Type, T> ringState, T t, long time);

    /**
     * 持续在该状态中
     *
     * @param ringState 环形状态机
     * @param t         状态机处理对象
     * @param time      当前时间
     */
    void on(RingState<Type, T> ringState, T t, long time);

    /**
     * 等待结束
     *
     * @param ringState 环形状态机
     * @param t         状态机处理对象
     * @param time      当前时间
     */
    void end(RingState<Type, T> ringState, T t, long time);

    /**
     * 状态超时（从开始状态到当前时间，超过wait时间）
     *
     * @param t    状态机处理对象
     * @param time 当前时间
     */
    void timeout(RingState<Type, T> ringState, T t, long time);

}

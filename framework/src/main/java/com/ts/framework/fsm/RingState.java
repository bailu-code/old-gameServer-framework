package com.ts.framework.fsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 环形状态机
 * @author wl
 */
public class RingState<Type, T> {
    private static Logger logger = LoggerFactory.getLogger(RingState.class);
    StateNode<Type> start;// 初始状态
    StateNode<Type> current;// 当前状态
    boolean inState;
    long startTime = 0;// 当前状态开始时间
    boolean finish = false;// 是否结束

    RingState() {
    }

    @SuppressWarnings("unchecked")
    private void in(T t, long time) {
        startTime = time;
        IState<Type, T> IState = (IState<Type, T>) StateManager.INSTANCE.get(current.type);
        IState.in(this, t, time);
    }

    @SuppressWarnings("unchecked")
    private void on(T t, long time) {
        IState<Type, T> IState = (IState<Type, T>) StateManager.INSTANCE.get(current.type);
        IState.on(this, t, time);
    }

    @SuppressWarnings("unchecked")
    private void timeout(T t, long time) {
        IState<Type, T> IState = (IState<Type, T>) StateManager.INSTANCE.get(current.type);
        IState.timeout(this, t, time);
    }

    @SuppressWarnings("unchecked")
    public void endCurrent(T t, long time) {
        IState<Type, T> IState = (IState<Type, T>) StateManager.INSTANCE.get(current.type);

        current = current.next;
        inState = false;

        IState.end(this, t, time);
    }

    /**
     * 获取当前状态剩余时间
     */
    public long getLeftTime() {
        return current.timeout - (System.currentTimeMillis() - startTime);
    }

    /**
     * 状态机是否结束
     */
    public void finish() {
        finish = true;
    }

    /**
     * 当前状态类型
     */
    public Type currentType() {
        return current.type;
    }

    /**
     * 状态更新
     */
    public void update(T t, long time) {
        if (finish) {
            return;
        }
        try {
            if (!inState) {
                startTime = time;
                inState = true;
                in(t, time);
            }
            if (time - startTime >= current.timeout) {
                timeout(t, time);
            } else {
                on(t, time);
            }
        } catch (Exception e) {
            logger.debug(t + " ring update error, finish: " + finish + ", current: " + (current == null ? null : current.type), e);
        }
    }

    /**
     * 重置状态
     */
    public void reset() {
        current = start;
        startTime = 0;
        inState = false;
        finish = false;
    }

    @Override
    public String toString() {
        return current == null ? "null" : current.type.toString();
    }

    /**
     * 状态机是否已经关闭
     */
    public boolean isFinish() {
        return finish;
    }

}

package com.ts.framework.fsm;

/**
 * 状态节点参数
 * @author wl
 */
class StateNode<Type> {
    Type type;
    StateNode<Type> next;
    long timeout;
}
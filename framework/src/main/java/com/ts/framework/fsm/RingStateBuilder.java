package com.ts.framework.fsm;

import java.util.ArrayList;

/**
 * 环形状态机构建器
 * @author wl
 */
public class RingStateBuilder<Type, T> {
    private ArrayList<StateNode<Type>> stateNodes = new ArrayList<>();

    public RingStateBuilder() {
    }

    /**
     * 添加一个状态节点
     *
     * @param type    状态类型
     * @param timeout 状态超时时间
     */
    public RingStateBuilder<Type, T> addNode(Type type, long timeout) {
        StateNode<Type> newNode = new StateNode<>();
        newNode.type = type;
        newNode.timeout = timeout;

        if (stateNodes == null) {
            stateNodes = new ArrayList<>();
        } else if (!stateNodes.isEmpty()) {
            stateNodes.get(stateNodes.size() - 1).next = newNode;
        }
        stateNodes.add(newNode);
        return this;
    }

    /**
     * 构建一个环形状态机
     */
    public RingState<Type, T> build() {
        RingState<Type, T> stageList = new RingState<>();
        stageList.start = stageList.current = stateNodes.get(0);
        return stageList;
    }

}
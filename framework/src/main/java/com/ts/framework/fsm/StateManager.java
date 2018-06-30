package com.ts.framework.fsm;

import com.ts.framework.script.ScriptSingleMap;
import org.springframework.stereotype.Component;

/**
 * 状态处理管理器
 * @author wl
 */
@Component
public class StateManager extends ScriptSingleMap<Object, IState<Object, ?>> {
    public static StateManager INSTANCE;

	@Override
	public Class parent() {
		return IState.class;
	}

	@Override
	public Object getKey(IState<Object, ?> objectIState) {
		return objectIState.stageType();
	}
}

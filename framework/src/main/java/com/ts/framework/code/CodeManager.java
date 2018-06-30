package com.ts.framework.code;

import com.ts.framework.script.ScriptProtoTypeMap;
import org.springframework.stereotype.Component;

/**
 * 网络通信消息
 */
@Component
public class CodeManager extends ScriptProtoTypeMap<Short, NetWork> {
	public static CodeManager INSTANCE;

	@Override
	public Class parent() {
		return NetWork.class;
	}

	@Override
	public Short getKey(NetWork netWork) {
		return netWork.code();
	}

}

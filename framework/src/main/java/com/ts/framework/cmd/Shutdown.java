package com.ts.framework.cmd;

import java.util.List;

/**
 * 关服
 * @author wl
 */
public class Shutdown implements ICmdHandler {

	@Override
	public String cmd() {
		return "shutdown";
	}

	@Override
	public void exc(String[] params, List<String> out) throws Exception {
		System.exit(0);
	}

	@Override
	public String desc() {
		return "关闭服务器";
	}

}

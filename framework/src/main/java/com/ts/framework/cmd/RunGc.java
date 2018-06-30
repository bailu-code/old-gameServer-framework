package com.ts.framework.cmd;

import java.util.List;

/**
 * gc
 * @author wl
 */
public class RunGc implements ICmdHandler {

	@Override
	public String cmd() {
		return "gc";
	}

	@Override
	public void exc(String[] params, List<String> out) throws Exception {
		System.gc();
	}

	@Override
	public String desc() {
		return "gc";
	}

}

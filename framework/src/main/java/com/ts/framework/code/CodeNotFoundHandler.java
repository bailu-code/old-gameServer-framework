package com.ts.framework.code;

import com.ts.framework.netty.NettyConnection;
import io.netty.buffer.ByteBuf;

/**
 * 消息号未找到处理类
 * @author wl
 */
public interface CodeNotFoundHandler {

	/**
	 * 消息号未找到处理类
	 */
	void codeNotFound(NettyConnection connection, int code, ByteBuf byteBuf);

}

package com.ts.framework.netty;

import io.netty.buffer.ByteBuf;

/**
 * 消息接收后的处理接口
 * @author wl
 */
public interface INettyHandler {

    /**
     * 消息处理
     */
    void messageReceived(NettyConnection connection, ByteBuf byteBuf);

    /**
     * 指定连接开启
     */
    void connectionCreated(NettyConnection connection);

    /**
     * 指定连接关闭
     */
    void connectionClosed(NettyConnection connection);

    /**
     * 连接空闲超时
     */
    void channelIdleTimeout(NettyConnection connection);

}

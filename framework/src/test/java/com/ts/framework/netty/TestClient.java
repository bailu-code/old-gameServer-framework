package com.ts.framework.netty;

import io.netty.buffer.ByteBuf;

/**
 * @author wl
 */
public class TestClient extends NettyClient {

    public static void main(String[] args) {
        TestClient testClient = new TestClient();
        testClient.connect("localhost", 4562, true);
    }

    @Override
    public void messageReceived(NettyConnection connection, ByteBuf byteBuf) {

    }

    @Override
    public void channelIdleTimeout(NettyConnection connection) {

    }

    @Override
    public void connectionCreated1(NettyConnection connection) {

    }

    @Override
    public void connectionClosed1(NettyConnection connection) {

    }
}

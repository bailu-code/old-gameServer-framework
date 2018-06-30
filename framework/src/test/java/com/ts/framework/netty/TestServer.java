package com.ts.framework.netty;

import io.netty.buffer.ByteBuf;

import java.util.concurrent.TimeUnit;

/**
 * @author wl
 */
public class TestServer extends NettyServer {

    public static void main(String[] args) throws InterruptedException {
        TestServer testServer = new TestServer();
        testServer.bind(4562);

        TimeUnit.HOURS.sleep(1);
    }

    @Override
    public void messageReceived(NettyConnection connection, ByteBuf byteBuf) {

    }

    @Override
    public void connectionCreated(NettyConnection connection) {

    }

    @Override
    public void connectionClosed(NettyConnection connection) {

    }

    @Override
    public void channelIdleTimeout(NettyConnection connection) {

    }
}

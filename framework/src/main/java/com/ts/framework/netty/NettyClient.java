package com.ts.framework.netty;

import com.google.protobuf.Message.Builder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * netty客户端,子类可重写createEventLoopGroup()方法，实现多个client共享同一个线程池
 * @author wl
 */
public abstract class NettyClient implements INettyHandler {
    protected static Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final int RECONNECT_SEC = 3;// 默认重连间隔
    private static final int MAX_MSG_PACK_SIZE = 100 * 1024;//接收消息包最大长度

    private Bootstrap b;
    private EventLoopGroup workerGroup;
    private int reConnectTime = 1;
    private int reConnectStep = 1;
    private boolean reconnect = true;// 断线自动重连
    private NettyConnection connection;// 与服务器的连接
    private String host;
    private int port;

    public NettyClient() {
    }

    /**
     * 连接服务器，默认断线重连
     *
     * @param host      ip
     * @param port      端口
     * @param reconnect 是否自动重连，重连间隔为5s
     */
    public void connect(String host, int port, boolean reconnect) {
        this.host = host;
        this.port = port;
        this.reconnect = reconnect;

        workerGroup = createEventLoopGroup();

        b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(MAX_MSG_PACK_SIZE, 0, 2));// 最大10M
                ch.pipeline().addLast(new LengthFieldPrepender(2));
                ch.pipeline().addLast(new NettyHandler(NettyClient.this));
            }
        }).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).option(
                ChannelOption.RCVBUF_ALLOCATOR,
                new AdaptiveRecvByteBufAllocator());

        // Start the client.
        connectTo(host, port, reconnect);
    }

    /**
     * 与服务器建立连接，支持重试
     *
     * @param host      ip
     * @param port      端口
     * @param reconnect 是否重连
     */
    private void connectTo(final String host, final int port, final boolean reconnect) {
        try {
            b.connect(host, port)
                    .addListener((ChannelFutureListener)
                            future -> {
                                if (!future.isSuccess() && reconnect) {
                                    scheduleReconnect();
                                }
                            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleReconnect() {
        workerGroup.schedule(() -> connectTo(host, port, reconnect), RECONNECT_SEC, TimeUnit.SECONDS);

        if (reConnectTime % reConnectStep == 0) {
            reConnectStep = reConnectTime;
            logger.error("client connect error! IP: {}, port: {}, reConnectTime = {}", host, port, reConnectTime);
        }
        ++reConnectTime;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }

    /**
     * 子类可重写此方法，实现多个client共享EventLoopGroup
     */
    public EventLoopGroup createEventLoopGroup() {
        return new NioEventLoopGroup(2);
    }

    @Override
    public final void connectionCreated(NettyConnection connection) {
        logger.debug("connect to {} success!", connection);

        this.connection = connection;
        connectionCreated1(connection);
    }

    @Override
    public final void connectionClosed(final NettyConnection connection) {
        logger.debug("disconnect to {}!", connection);

        connectionClosed1(connection);
        if (reconnect) {
            scheduleReconnect();
        }
    }

    public abstract void connectionCreated1(NettyConnection connection);

    public abstract void connectionClosed1(NettyConnection connection);

    /**
     * 发送消息
     */
    public void sendMsg(ByteBuf byteBuf) {
        if (connection != null) {
            connection.sendMsg(byteBuf);
        }
    }

    /**
     * 发送消息
     */
    public void sendMsg(byte[] data) {
        if (connection != null) {
            connection.sendMsg(data);
        }
    }

    /**
     * 发送空消息
     */
    public void sendMsg(int code) {
        if (connection != null) {
            connection.sendMsg(code);
        }
    }

    /**
     * 发送消息
     *
     * @param code 消息号
     * @param data 数据
     */
    public void sendMsg(int code, byte[] data) {
        if (connection != null) {
            connection.sendMsg(code, data);
        }
    }

    /**
     * 发送消息
     *
     * @param code    消息号
     * @param builder 消息体
     */
    public void sendMsg(int code, Builder builder) {
        if (connection != null) {
            connection.sendMsg(code, builder);
        }
    }

    /**
     * 创建一个消息缓存
     */
    public ByteBuf createBuf(int code) {
        return connection.createBuf(code);
    }

    /**
     * 创建一个消息缓存
     */
    public ByteBuf createBuf(int code, int len) {
        return connection.createBuf(code, len);
    }

    /**
     * ip地址
     */
    public String ip() {
        return connection.getIp();
    }

    /**
     * 端口
     */
    public int port() {
        return connection.getPort();
    }

    /**
     * 断开连接
     */
    public void disConnect() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    public NettyConnection connection() {
        return connection;
    }

    public boolean isConnected() {
        return connection != null && connection.isActive();
    }

    public void setConnection(NettyConnection connection) {
        this.connection = connection;
    }

}

package com.ts.framework.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ipfilter.UniqueIpFilter;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.DefaultAttributeMap;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * netty服务器
 *
 * @author wl
 */
public abstract class NettyServer extends DefaultAttributeMap implements INettyHandler {
    protected static Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private static final int MAX_MSG_PACK_SIZE = 100 * 1024;//接收消息包最大长度
    private static final int IDLE_TIMEOUT_SECENDS = 3 * 60;//连接空闲超时时间

    private int port;
    private ChannelFuture serverChannel;
    private EventLoopGroup workGroup;
    private boolean isRunning = false;

    /**
     * 开启server
     *
     * @param port 端口号
     */
    public void bind(int port) {
        this.port = port;

        workGroup = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();

        server
                .group(workGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                // 指定使用NioServerSocketChannel类,
                // 这个类被用来初始化一个新的Channel以接用连上来的连接.
                .childHandler(new ChannelInitializer<SocketChannel>() {// 消息处理链

                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                //可做心跳检测，一定时间内，没有数据通讯，则认为超时
                                new IdleStateHandler(0, 0, IDLE_TIMEOUT_SECENDS, TimeUnit.SECONDS),

                                new UniqueIpFilter(),
                                // 接受包包长处理
                                new LengthFieldBasedFrameDecoder(MAX_MSG_PACK_SIZE, 0, 2),
                                //发送包长度
                                new LengthFieldPrepender(2),
                                //连接处理
                                new NettyHandler(NettyServer.this)
                        );
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 1000)//请求连接队列的最大数量，超过将被拒绝连接
                .option(ChannelOption.SO_KEEPALIVE, true)// 保持连接检测对方主机是否崩溃,避免(服务器)永远阻塞于TCP连接的输入
                .option(ChannelOption.TCP_NODELAY, true)// 关闭小包优化
                .option(ChannelOption.SO_TIMEOUT, 1)// 读取，写入 超时时间
                .option(ChannelOption.SO_REUSEADDR, true)// socket可重用
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)// 开启缓存池（netty4.1开始已经默认）
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator())//接收消息包缓存池

                .childOption(ChannelOption.SO_KEEPALIVE, true)// 保持连接检测对方主机是否崩溃,避免(服务器)永远阻塞于TCP连接的输入
                .childOption(ChannelOption.TCP_NODELAY, true)// 关闭小包优化
                .childOption(ChannelOption.SO_REUSEADDR, true)// socket可重用
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)// 开启缓存池（netty4.1开始已经默认）
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator());//接收消息包缓存池

        try {
            serverChannel = server.bind(port).sync();// 可多次调用绑定多个端口
        } catch (InterruptedException e) {
            return;//被终止
        } catch (Exception e) {
            if (workGroup != null) {
                workGroup.shutdownGracefully();
            }
            throw e;
        }

        isRunning = true;

        logger.warn("server bind {}", port);
    }

    /**
     * 关闭
     */
    public void shutdown() {
        if (serverChannel != null) {
            serverChannel.channel().close();
        }
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
        isRunning = false;
        logger.warn("net server shutdown!");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getPort() {
        return port;
    }

}

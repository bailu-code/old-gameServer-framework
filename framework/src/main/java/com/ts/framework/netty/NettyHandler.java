package com.ts.framework.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;

/**
 * 消息分发，连接管理
 * @author wl
 */
@Sharable
class NettyHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyHandler.class);
    private static final AttributeKey<NettyConnection> NET_CONNECTION_KEY = AttributeKey.valueOf("NettyConnection");

    private INettyHandler nettyHandler;//消息处理接口

    NettyHandler(INettyHandler nettyHandler) {
        this.nettyHandler = nettyHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 新建连接
        NettyConnection nettyConnection = new NettyConnection(ctx);
        ctx.channel().attr(NET_CONNECTION_KEY).set(nettyConnection);

        // 调用连接打开的接口
        try {
            nettyHandler.connectionCreated(nettyConnection);
        } catch (Exception e) {
            logger.error("connectionCreated error! ", e);
        }

        logger.debug("new connection: {}", nettyConnection);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            NettyConnection nettyConnection = ctx.channel().attr(NET_CONNECTION_KEY).get();
            if (nettyConnection == null) {
                logger.error("messageReceived error, nettyConnection = null!");
                return;
            }
            if (msg == null) {
                logger.error("messageReceived error, msg = null!");
                return;
            }
            // 分发
            nettyHandler.messageReceived(nettyConnection, (ByteBuf) msg);
        } catch (Exception e) {
            logger.error("channelRead error: ", e);
        } finally {
            ReferenceCountUtil.release(msg);// 释放ByteBuf
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();

        NettyConnection nettyConnection = ctx.channel().attr(NET_CONNECTION_KEY).get();
        nettyConnection.setDisconnectTime(System.currentTimeMillis());

        // 调用连接断开的接口
        try {
            nettyHandler.connectionClosed(nettyConnection);
        } catch (Exception e) {
            logger.error("connectionClosed error! ", e);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    logger.warn("{} reader timeout!", ctx.channel().remoteAddress());
                    break;
                case WRITER_IDLE:
                    logger.warn("{} writer timeout!", ctx.channel().remoteAddress());
                    break;
                case ALL_IDLE:
                    NettyConnection nettyConnection = ctx.channel().attr(NET_CONNECTION_KEY).get();
                    if (nettyConnection != null){
                        nettyHandler.channelIdleTimeout(nettyConnection);
                    }
                    logger.warn("{} all timeout!", ctx.channel());
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        NettyConnection nettyConnection = ctx.channel().attr(NET_CONNECTION_KEY).get();
        if (nettyConnection == null) {
            return;
        }
        if ("远程主机强迫关闭了一个现有的连接。".equals(cause.getLocalizedMessage())) {
            logger.error("远程主机强迫关闭了一个现有的连接! " + nettyConnection.toString());
        } else if ("Connection reset by peer".equals(cause.getLocalizedMessage())) {
            logger.error("Connection reset by peer! " + nettyConnection.toString());
        } else if ("Connection timed out".equals(cause.getLocalizedMessage())) {
            logger.error("Connection timed out" + nettyConnection.toString());
        } else {
            logger.error(nettyConnection + " exceptionCaught: {}", cause);
        }
    }

}

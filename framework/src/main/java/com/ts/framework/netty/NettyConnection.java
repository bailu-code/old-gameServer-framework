package com.ts.framework.netty;

import com.google.protobuf.Message.Builder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.ArrayUtils;

import java.net.SocketAddress;

/**
 * 连接维护
 * @author wl
 */
public class NettyConnection {
    private static final int MAX_PING = 1000;
    private String ip;
    private int port;
    private ChannelHandlerContext ctx;

    private int ping;// 角色ping值
    private long connectTime;// 连接时间
    private long disconnectTime;//断开连接时间

    public NettyConnection(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.connectTime = System.currentTimeMillis();

        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        if (remoteAddress != null) {
            String[] remote = remoteAddress.toString().replace("/", "").split(":");
            this.ip = remote[0];
            this.port = Integer.valueOf(remote[1]);
        }
    }

    public Channel channel() {
        return ctx.channel();
    }

    public int getPort() {
        return port;
    }

    /**
     * 获取附件
     */
    public <T> T get(AttributeKey<T> key) {
        return ctx.channel().attr(key).get();
    }

    /**
     * 移除附件
     */
    public void remove(AttributeKey<?> key) {
        ctx.channel().attr(key).set(null);
    }

    /**
     * 设置附件
     */
    public <T> void set(AttributeKey<T> key, T value) {
        ctx.channel().attr(key).set(value);
    }

    /**
     * 如果重复调用该方法发送同一个ByteBuf，需要注意引用计数的问题
     */
    public void sendMsg(ByteBuf byteBuf) {
        sendMsg(byteBuf, true);
    }

    /**
     * 如果重复调用该方法发送同一个ByteBuf，需要注意引用计数的问题
     * @param flush 是否立即发送
     */
    public void sendMsg(ByteBuf byteBuf, boolean flush) {
        if (byteBuf == null) {
            return;
        }
        if (!isActive()) {
            byteBuf.release();
            return;
        }
        if (flush) {
            ctx.writeAndFlush(byteBuf);
        } else {
            ctx.write(byteBuf);
        }
    }

    /**
     * 发送消息
     */
    public void sendMsg(byte[] data) {
        sendMsg(createBufWithLen(data.length).writeBytes(data));
    }

    /**
     * 发送空消息
     *
     * @param code 消息号
     */
    public void sendMsg(int code) {
        sendMsg(code, ArrayUtils.EMPTY_BYTE_ARRAY);
    }

    /**
     * 发送消息
     *
     * @param code 消息号
     * @param data 数据
     */
    public void sendMsg(int code, byte[] data) {
        sendMsg(createBuf(code, data.length + 4).writeBytes(data));// 释放buf
    }

    /**
     * 发送消息
     *
     * @param code    消息号
     * @param builder 消息体
     */
    public void sendMsg(int code, Builder builder) {
        sendMsg(code, builder.build().toByteArray());
    }

    /**
     * 创建一个包含消息号的消息体，默认预设长度
     *
     * @param code 消息号
     * @return 创建的消息体
     */
    public ByteBuf createBuf(int code) {
        return createBufWithLen(4).writeInt(code);
    }

    /**
     * 创建一个消息体，默认的预设长度
     *
     * @return 创建的消息体
     */
    public ByteBuf createBuf() {
        return ctx.alloc().buffer();
    }

    /**
     * 创建一个消息体
     *
     * @param capacity 预留空间大小
     * @return 创建的消息体
     */
    public ByteBuf createBufWithLen(int capacity) {
        return ctx.alloc().buffer(capacity);
    }

    /**
     * 创建一个即将发送的消息体
     *
     * @param code     消息号
     * @param capacity 预留空间大小
     * @return 创建的消息体
     */
    public ByteBuf createBuf(int code, int capacity) {
        return createBufWithLen(capacity + 4).writeInt(code);
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        ctx.close();
    }

    /**
     * 检测连接是否为活动状态
     *
     * @return true 活动，false 已断开
     */
    public boolean isActive() {
        return ctx.channel().isActive();
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        if (ping < 0) {
            this.ping = 0;
        } else if (ping > MAX_PING) {
            this.ping = MAX_PING;
        }
    }

    public long getConnectTime() {
        return connectTime;
    }

    public long getDisconnectTime() {
        return disconnectTime;
    }

    public void setDisconnectTime(long disconnectTime) {
        this.disconnectTime = disconnectTime;
    }


}

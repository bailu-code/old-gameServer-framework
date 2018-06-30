package com.ts.framework.code;

import com.ts.framework.netty.NettyConnection;
import com.ts.framework.work.Args;
import com.ts.framework.work.QueueWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

/**
 * 网络消息处理基类
 *
 * @author wl
 */
public abstract class NetWork<Msg> extends QueueWork {
    protected Logger logger = LoggerFactory.getLogger(getClass());// 供子类使用
    private NettyConnection connection;
    private Msg msg;

    @Override
    public final void init(Args args) {
    }

    /**
     * 消息处理类初始化方法
     */
    public final void init(NettyConnection connection, short code, ByteBuf byteBuf) throws Exception {
        this.connection = connection;

        this.msg = parseMsg(code, byteBuf);

        ReferenceCountUtil.retain(msg);
    }

    /**
     * 解析消息
     *
     * @param code    消息号
     * @param byteBuf 消息数据
     * @return 解析后的消息体
     */
    public abstract Msg parseMsg(short code, ByteBuf byteBuf);

    /**
     * 消息处理接口
     */
    public abstract void handle(NettyConnection con, Msg msg) throws Exception;

    /**
     * 消息号
     */
    public abstract short code();

    /**
     * 是否输出接收到消息的日志
     */
    public boolean debug() {
        // do nothing
        return false;
    }

    /**
     * 跳转后的调用，step表示跳转的次数，从1开始
     */
    public void runStep(int step) {
        // do nothing
        logger.error("jumpQueue {} run step {}, but doNothing!" + getClass().getSimpleName(), step);
    }

    @Override
    public final void run(int step) {
        // 执行
        try {
            if (step == 0) {// 首次调用消息处理
                handle(connection, msg);
            } else {// 跳转调用
                runStep(step);
            }
        } catch (Exception e) {
            logger.error("handle error! ip: " + connection.getIp(), e);
        } finally {
            ReferenceCountUtil.release(msg);// 释放byteBuf
        }
    }

    public NettyConnection getConnection() {
        return connection;
    }

    public Msg getMsg() {
        return msg;
    }

    public Logger getLogger() {
        return logger;
    }

}

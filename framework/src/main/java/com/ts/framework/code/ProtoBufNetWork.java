package com.ts.framework.code;

import com.google.protobuf.Message;
import com.ts.framework.helper.ClassHelper;
import io.netty.buffer.ByteBuf;

/**
 * protoBuf网络消息
 *
 * @author wl
 */
public abstract class ProtoBufNetWork<Msg extends Message> extends NetWork<Msg> {
    private static ThreadLocal<Message.Builder> msgBuilder = new ThreadLocal<>();

    @SuppressWarnings("unchecked")
    @Override
    public Msg parseMsg(short code, ByteBuf byteBuf) {
        Message.Builder builder = null;
        try {
            builder = msgBuilder.get();
            if (builder == null) {
                Class msgClass = ClassHelper.getSuperClassGenericType(this.getClass(), 0);
                builder = (Message.Builder) msgClass.getMethod("newBuilder").invoke(null);
                msgBuilder.set(builder);
            }
            int len = byteBuf.readableBytes();
            if (len <= 0) {
                return (Msg) builder.build();
            }
            return (Msg) builder.mergeFrom(byteBuf.readBytes(len).array()).build();
        } catch (Exception e) {
            throw new RuntimeException("error protoBuf msg class, not have method newBuilder()");
        } finally {
            if (builder != null) {
                builder.clear();
            }
        }
    }
}

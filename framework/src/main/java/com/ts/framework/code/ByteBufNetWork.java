package com.ts.framework.code;

import io.netty.buffer.ByteBuf;

/**
 * byteBuf网络消息
 *
 * @author wl
 */
public abstract class ByteBufNetWork extends NetWork<ByteBuf> {

    @Override
    public ByteBuf parseMsg(short code, ByteBuf byteBuf) {
        return byteBuf;
    }

}

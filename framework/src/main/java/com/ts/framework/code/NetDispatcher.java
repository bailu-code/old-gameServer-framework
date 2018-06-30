package com.ts.framework.code;

import com.ts.framework.netty.NettyConnection;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息分发工具，用于辅助codeManager使用
 *
 * @author wl
 */
public class NetDispatcher {
    private static Logger logger = LoggerFactory.getLogger(NetDispatcher.class);

    /**
     * 分发消息
     */
    public static void messageReceived(NettyConnection connection, ByteBuf byteBuf, CodeNotFoundHandler codeNotFoundHandler) {
        // 消息号
        if (byteBuf.readableBytes() < 2) {
            throw new RuntimeException("byteBuf.readableBytes < 2, miss code! ip: " + connection.getIp());
        }
        // 消息号
        short code = byteBuf.readShort();
        try {
            NetWork<?> netWork = CodeManager.INSTANCE.build(code);
            if (netWork == null) {
                codeNotFoundHandler.codeNotFound(connection, code, byteBuf);
                return;
            }

            netWork.init(connection, code, byteBuf);

//            WorkManager.INSTANCE.submit(netWork);

            // 输出调试信息
            if (netWork.debug()) {
                logger.info("{} by code: {}", netWork.getClass().getSimpleName(), code);
            }
        } catch (Exception e) {
            logger.error(connection.getIp() + ", code = " + code, e);
        }
    }

}

package com.ts.framework.cmd;

import java.util.List;

/**
 * 命令处理接口
 * @author wl
 */
public interface ICmdHandler {

    /**
     * 命令号
     */
    String cmd();

    /**
     * 执行命令
     *
     * @param params 参数
     * @param out    输出结果
     * @throws Exception 命令执行异常
     */
    void exc(String[] params, List<String> out) throws Exception;

    /**
     * @return 命令描述
     */
    String desc();

}

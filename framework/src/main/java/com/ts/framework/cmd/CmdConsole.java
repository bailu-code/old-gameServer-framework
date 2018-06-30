package com.ts.framework.cmd;

import com.ts.framework.script.ScriptSingleMap;
import org.springframework.stereotype.Component;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 命令行控制台
 * @author wl
 */
@Component
public class CmdConsole extends ScriptSingleMap<String, ICmdHandler> implements UncaughtExceptionHandler {
    public static CmdConsole INSTANCE;

    private CmdExcThread cmdExcThread = null;

    public CmdConsole() {
        inject(new ReloadClass());
        inject(new RunGc());
        inject(new Shutdown());
    }

    @Override
    public Class parent() {
        return ICmdHandler.class;
    }

    @Override
    public String getKey(ICmdHandler value) {
        return value.cmd();
    }

    /**
     * 开启命令行控制台
     */
    public void start() {
        cmdExcThread = new CmdExcThread();
        cmdExcThread.setUncaughtExceptionHandler(this);
        cmdExcThread.start();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        start();
    }

    /**
     * 关闭
     */
    public void close() {
        if (cmdExcThread != null) {
            cmdExcThread.shutdown();
        }
    }

}



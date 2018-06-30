package com.ts.framework.cmd;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * 命令执行线程
 * @author wl
 */
class CmdExcThread extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdExcThread.class);
    private boolean run = true;

    public void shutdown() {
        run = false;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String cmd = null;

        System.out.println("cmdConsole start!");
        while (run) {
            try {
                System.out.println("cmd> ");
                if (scanner.hasNextLine()) {// 读取命令行
                    cmd = scanner.nextLine().trim();
                    if (cmd.isEmpty()) {
                        System.out.println("cmd is empty, use [help] to show all cmd!");
                        continue;
                    }

                    exc(cmd).forEach(System.out::println);
                } else {
                    LOGGER.error("cmdConsole is closed, restart now");
                    CmdConsole.INSTANCE.start();
                    break;
                }
            } catch (Exception e) {
                LOGGER.error(cmd + " exc error", e);
            }
        }
        scanner.close();
    }

    /**
     * 执行命令
     */
    public List<String> exc(String str) throws Exception {
        if (StringUtils.isEmpty(str)) {
            return showHelp();// 空命令
        }
        String[] params = str.split(" ");
        String cmd = params[0];
        if ("help".equals(cmd)) {
            return showHelp();// 查看帮助
        }
        ICmdHandler handler = CmdConsole.INSTANCE.get(cmd);
        if (handler == null) {
            showHelp();
            return showHelp();// 无法识别的命令
        }
        ArrayList<String> out = new ArrayList<>(3);
        handler.exc(ArrayUtils.subarray(params, 1, params.length), out); // 运行命令
        out.add("cmd: " + str + " exc success");
        return out;
    }

    /**
     * 打印出帮助列表
     */
    public List<String> showHelp() {
        ArrayList<String> out = new ArrayList<>();
        Collection<ICmdHandler> all = CmdConsole.INSTANCE.getAll();
        out.add("there is " + all.size() + " cmd register");
        all.forEach(cmdHandler -> out.add("[" + cmdHandler.cmd() + "]" + ": " + cmdHandler.desc()));
        return out;
    }
}
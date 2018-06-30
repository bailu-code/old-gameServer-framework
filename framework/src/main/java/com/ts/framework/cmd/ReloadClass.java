package com.ts.framework.cmd;

import com.ts.framework.helper.ScannerHelper;
import com.ts.framework.script.ScriptManager;
import com.ts.framework.spring.Context;

import java.nio.file.Paths;
import java.util.List;

/**
 * 重新加载class文件
 *
 * @author wl
 */
public class ReloadClass implements ICmdHandler {

    @Override
    public String cmd() {
        return "rc";
    }

    @Override
    public void exc(String[] params, List<String> out) throws Exception {
        ScriptManager scriptManager = Context.getBean(ScriptManager.class);
        if (params.length == 0) {
            scriptManager.reload();
        } else {
            for (String className : params) {
                className = ScannerHelper.toPath(className) + ".class";
                if (Paths.get(className).toFile().exists()) {
                    scriptManager.reloadScript(className);

                    System.out.println("reload " + className);
                } else {
                    System.out.println("not found " + className);
                }
            }
        }
    }

    @Override
    public String desc() {
        return "重新加载class";
    }

}

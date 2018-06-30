package com.ts.framework.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表类脚本，每次获取已列表的方式获取
 * @author wl
 */
public abstract class ScriptList<Script> implements ScriptBox {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private List<Script> scripts = new ArrayList<>();// 缓存对象

    @SuppressWarnings("unchecked")
    @Override
    public void inject(Object script) {
        scripts.add((Script) script);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reInject(Object oldScript, Object newScript) {
        for (int i = 0; i < scripts.size(); i++) {
            Script script = scripts.get(i);
            if (script == oldScript) {
                scripts.set(i, (Script) newScript);
                break;
            }
        }
    }

    @Override
    public void scanOver() {
        logger.info("↓ cache list script num {} ↓", scripts.size());
        scripts.forEach(script -> logger.info(script.getClass().getSimpleName()));
        logger.info("↑ cache list script ↑");
    }

    /**
     * 注册的数量
     */
    public int size() {
        return scripts.size();
    }

    /**
     * 获取所有脚本
     */
    public List<Script> getAll() {
        return scripts;
    }


}

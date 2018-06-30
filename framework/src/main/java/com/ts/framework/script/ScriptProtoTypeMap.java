package com.ts.framework.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Map类脚本，每次获取已key获取，原型模式
 *
 * @author wl
 */
public abstract class ScriptProtoTypeMap<Key, Script> implements ScriptBox {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<Key, Class<Script>> scriptMap = new ConcurrentHashMap<>();// 脚本缓存

    /**
     * 该脚本的key值
     */
    public abstract Key getKey(Script script);

    @SuppressWarnings("unchecked")
    @Override
    public void inject(Object scriptObj) {
        Script script = (Script) scriptObj;
        Key key = getKey(script);
        if (key == null) {
            throw new NullPointerException("script " + scriptObj.getClass() + " key is null");
        }
        scriptMap.put(key, (Class<Script>) script.getClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reInject(Object oldScriptObj, Object newScriptObj) {
        Script oldScript = (Script) oldScriptObj;
        Script newScript = (Script) newScriptObj;
        Key key = getKey(newScript);
        if (key == null) {
            throw new NullPointerException("newScript " + newScript.getClass() + " key is null");
        }
        scriptMap.remove(getKey(oldScript));
        scriptMap.put(key, (Class<Script>) newScript.getClass());
    }

    @Override
    public void scanOver() {
        logger.info("↓ cache map script num {} ↓", scriptMap.size());
        scriptMap.forEach((key, script) -> logger.info("{}: {}", key, script.getClass().getSimpleName()));
        logger.info("↑ cache map class ↑");
    }

    /**
     * 是否存在指定脚本
     * @param key 脚本key
     * @return true 存在，false不存在
     */
    public boolean containsKey(Key key) {
        return scriptMap.containsKey(key);
    }

    /**
     * 获取脚本
     *
     * @param key 脚本key值
     * @return 如果没有该脚本，则返回null
     */
    public Script build(Key key) {
        Class<Script> script = scriptMap.get(key);
        if (script == null) {
            return null;
        }
        try {
            return script.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

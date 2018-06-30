package com.ts.framework.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Map类脚本，每次获取已key获取
 *
 * @author wl
 */
public abstract class ScriptSingleMap<Key, Script> implements ScriptBox {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<Key, Script> scriptMap = new ConcurrentHashMap<>();// 脚本缓存

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
        scriptMap.put(key, script);
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
        scriptMap.put(key, newScript);
    }

    @Override
    public void scanOver() {
        logger.info("↓ cache map script num {} ↓", scriptMap.size());
        scriptMap.forEach((key, script) -> logger.info("{}: {}", key, script.getClass().getSimpleName()));
        logger.info("↑ cache map class ↑");
    }

    /**
     * 获取脚本
     *
     * @param key 脚本key值
     * @return 如果没有该脚本，则返回null
     */
    public Script get(Key key) {
        return scriptMap.get(key);
    }

    /**
     * 获取所有脚本
     */
    public Collection<Script> getAll() {
        return scriptMap.values();
    }

}

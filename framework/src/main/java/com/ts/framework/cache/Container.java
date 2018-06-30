package com.ts.framework.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 容器
 * @author wl
 */
public class Container<Key, Data> {
    private Map<Key, Data> map = new ConcurrentHashMap<>();

    /**
     * 数量
     */
    public int size() {
        return map.size();
    }

    /**
     * 获取
     */
    public Data get(Key key) {
        return map.get(key);
    }

    /**
     * 添加
     */
    public void put(Key key, Data data) {
        map.put(key, data);
    }

    /**
     * 删除
     */
    public Data remove(Key key) {
        return map.remove(key);
    }

    /**
     * 所有数据
     */
    public Collection<Data> all() {
        return map.values();
    }

}

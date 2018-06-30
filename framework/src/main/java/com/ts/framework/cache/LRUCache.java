package com.ts.framework.cache;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 简略角色信息缓存
 * @author wl
 */
public class LRUCache<Key, Data> implements EvictionListener<Key, Data> {
    private static Logger logger = LoggerFactory.getLogger(LRUCache.class);
    private Map<Key, Data> cache = null;// 缓存
    private Set<Key> loadFailSet = new HashSet<>();//数据加载失败的标记，避免重复加载无法加载的数据
    private ICacheLoader<Key, Data> loader;
    private ICacheRemover<Key, Data> remover;

    private LRUCache() {
    }

    /**
     * 构建一个LRUCache
     *
     * @param initCapacity 初始容量
     * @param maxCapacity  最大容量
     * @param loader       数据加载接口
     * @param remover      数据移除接口
     * @param <Key>        数据key类型
     * @param <Data>       数据data类型
     * @return 构建好的LRUCache
     */
    public static <Key, Data> LRUCache<Key, Data> build(int initCapacity, int maxCapacity, ICacheLoader<Key, Data> loader, ICacheRemover<Key, Data> remover) {
        LRUCache<Key, Data> lruCache = new LRUCache<>();
        lruCache.cache = new ConcurrentLinkedHashMap.Builder<Key, Data>()
                .listener(lruCache)
                .initialCapacity(initCapacity)
                .maximumWeightedCapacity(maxCapacity)
                .build();
        lruCache.loader = loader;
        lruCache.remover = remover;
        return lruCache;
    }

    @Override
    public void onEviction(Key key, Data value) {
        try {
            remover.onRemove(value);
        } catch (Exception e) {
            logger.error("cache remover listen error, key: " + key + ", value: " + value, e);
        }
    }

    /**
     * 查找数据，缓存未命中会调用{@link ICacheLoader#load(Object)}进行加载（只加载一次，失败后都会返回null，除非调用了{@link #put(Object, Object)}放入数据）
     */
    public Data find(Key key) {
        Data data = cache.get(key);
        if (data == null && !loadFailSet.contains(key)) {
            try {
                data = loader.load(key);
            } catch (Exception e) {
                logger.error("cache loader error, key: " + key, e);
            }
            if (data == null) {
                loadFailSet.add(key);// 加载失败的资源做记录，避免二次查找
            } else {
                cache.put(key, data);// 放入缓存
            }
        }
        return data;
    }

    /**
     * 主动放入数据，放入后会尝试清除loadFail标记
     */
    public void put(Key key, Data data) {
        cache.put(key, data);// 放入缓存
        loadFailSet.remove(key);// 如果有查询失败记录，则删除掉
    }

    /**
     * 所有数据
     */
    public Collection<Data> all() {
        return cache.values();
    }

    /**
     * 数量
     */
    public int size() {
        return cache.size();
    }

}
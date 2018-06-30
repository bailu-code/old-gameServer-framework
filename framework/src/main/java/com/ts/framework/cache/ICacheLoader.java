package com.ts.framework.cache;

/**
 * 数据加载接口
 * @author wl
 */
public interface ICacheLoader<Key, Data> {

    /**
     * 当缓存中不存在时的加载接口，注意，该数据查询如果返回null，则后续查询不再调用该接口，避免对不存在的数据多次重复查询
     */
    Data load(Key key) throws Exception;
}

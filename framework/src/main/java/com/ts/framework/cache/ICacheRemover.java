package com.ts.framework.cache;

/**
 * 数据被移除时的接口
 * @author wl
 */
public interface ICacheRemover<Key, Data> {

    /**
     * 缓存过期
     */
    void onRemove(Data data) throws Exception;

}
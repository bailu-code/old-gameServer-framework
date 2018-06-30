package com.ts.framework.file;

/**
 * 文件监听者
 * @author wl
 */
public interface IFileObserver {

    /**
     * 文件被修改
     */
    void modify(String fileName);

}
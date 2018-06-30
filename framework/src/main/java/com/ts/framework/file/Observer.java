package com.ts.framework.file;

import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class Observer {
    Path filePath;// 监听路径
    String suffix;// 监听文件的后缀
    IFileObserver observer;// 监听回调
    WatchService watchService;// 监听服务
    Map<String, Long> modifyMap = new HashMap<>();//文件改变事件缓存，用于过滤相同事件
    int delay = 2000;

    void notifyModify(long now) {
        Iterator<Map.Entry<String, Long>> iterator = modifyMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> next = iterator.next();
            if (now - next.getValue() > delay) {
                observer.modify(next.getKey());
                iterator.remove();
            }
        }
    }
}
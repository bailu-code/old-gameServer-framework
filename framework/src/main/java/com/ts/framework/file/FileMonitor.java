package com.ts.framework.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * 文件监听器
 * @author wl
 */
public class FileMonitor {
    private static Logger logger = LoggerFactory.getLogger(FileMonitor.class);

    private static WatcherThread watcherThread;
    private static int interval = 1;//检测间隔，单位s

    /**
     * 设置文件检测间隔，单位s，全局唯一变量，所有文件检测都会使用该值
     *
     * @param interval 单位s
     */
    public static void setInterval(int interval) {
        FileMonitor.interval = interval;
    }

    /**
     * 监听指定目录、指定后缀名的文件
     *
     * @param filePath 文件目录
     * @param suffix   后缀
     * @param listener 监听器
     */
    public static void watch(final Path filePath, final String suffix, final IFileObserver listener) throws IOException {
        if (watcherThread == null) {
            watcherThread = new WatcherThread(interval);
            // 开始监控
            watcherThread.start();

            watcherThread.setUncaughtExceptionHandler((t, e) -> {
                try {
                    t.interrupt();
                    List<Observer> observers = watcherThread.getObservers();

                    watcherThread = null;
                    for (Observer observer : observers) {
                        watch(observer.filePath, observer.suffix, observer.observer);
                    }

                    logger.debug("watch thread dead, reWatch", e);
                } catch (IOException e1) {
                    logger.error("reWatch error", e);
                }
            });

        }
        watcherThread.watch(filePath, suffix, listener);
    }

}


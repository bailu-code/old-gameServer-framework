package com.ts.framework.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 文件更改监听线程
 * @author wl
 */
class WatcherThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(WatcherThread.class);
    private List<Observer> observers = new ArrayList<>();// 监听列表
    private int interval;

    public WatcherThread(int interval) throws IOException {
        this.interval = interval;
    }

    public void watch(Path filePath, String suffix, IFileObserver listener) throws IOException {
        Observer observer = new Observer();
        observer.filePath = filePath;
        observer.suffix = suffix;
        observer.observer = listener;
        observer.watchService = FileSystems.getDefault().newWatchService();

        register(observer.watchService, filePath);

        observers.add(observer);
    }

    private void register(final WatchService watchService, Path filePath) throws IOException {
        Files.walkFileTree(filePath, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    @Override
    public void run() {
        WatchKey watchKey;
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(interval);
            } catch (InterruptedException e1) {
                return;
            }

            long now = System.currentTimeMillis();
            for (Observer observer : observers) {
                watchKey = observer.watchService.poll();

                if (watchKey == null) {
                    observer.notifyModify(now);
                    continue;
                }
                try {
                    List<WatchEvent<?>> events = watchKey.pollEvents();
                    for (WatchEvent<?> event : events) {
                        String fileName = watchKey.watchable() + File.separator + event.context().toString();
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            register(observer.watchService, Paths.get(fileName));
                        }
                        if (!fileName.endsWith(observer.suffix)) {
                            continue;
                        }

                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            observer.modifyMap.put(fileName, System.currentTimeMillis());
//                            } else if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
//                                observer.observer.create(fileName);
//                            } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
//                                observer.observer.delete(fileName);
                        }
                    }
                } catch (IOException e) {
                    logger.error("observer: " + observer.getClass(), e);
                }
                watchKey.reset();
            }
        }
    }

    public List<Observer> getObservers() {
        return observers;
    }
}
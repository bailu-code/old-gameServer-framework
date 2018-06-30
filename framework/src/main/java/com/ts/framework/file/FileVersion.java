package com.ts.framework.file;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 文件版本管理
 * @author wl
 */
public class FileVersion {

    private static Map<String, Long> versionMap = new HashMap<>();

    /**
     * 文件是否已被标记过
     *
     * @param path 文件
     * @return 已经标记过并且版本号一致，则返回true，否则false
     */
    public static boolean isRecord(Path path) {
        File file = path.toFile();
        return Objects.equals(versionMap.get(file.getName()), file.lastModified());
    }

    /**
     * 记录文件版本信息，如果文件版本已存在并且{@link File#lastModified()}与已存在的版本一致，则记录失败
     *
     * @param path 文件
     * @return 版本没有变化记录失败false，以前没有版本或者版本变化了记录成功true
     */
    public static boolean record(Path path) {
        File file = path.toFile();
        Long oldModify = versionMap.put(file.getName(), file.lastModified());
        return !Objects.equals(oldModify, file.lastModified());
    }


}

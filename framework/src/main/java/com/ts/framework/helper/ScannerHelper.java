package com.ts.framework.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 文件扫描工具
 * @author wl
 */
public class ScannerHelper {
    private static String BIN_PREFIX = File.separatorChar + "bin" + File.separatorChar;
    private static String CLASS_SUFFIX = ".class";

    /**
     * class文件扫描过滤器
     * @author wl
     */
    public interface IClassFilter {
        /**
         * 是否满足条件
         */
        boolean pass(Class clz);

    }

    public static void main(String[] args) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Set<Class<?>> set = scanClass("com.ts.core", false, clz -> ClassHelper.checkInterface(clz, Runnable.class));
            set.forEach(aClass -> {
                if (aClass.isAnnotation()) {
                    System.out.println(true);
                }
            });
            stopWatch.stop();
            System.out.println(stopWatch.getTime());

//            MyClassLoader.INSTANCE().load(false, "com.core");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将包名转换为完整目录名
     */
    public static Path toPath(String packageName) {
        String path;
        URL url = ClassLoader.getSystemResource("");
        if (url == null) {
            path = System.getProperty("user.dir");
        } else {
            path = url.getPath();
            System.out.println(path);
            if (path.contains(":")) {
                path = path.substring(1);
            } else {
                path = File.separatorChar + path.substring(1);
            }
        }
        return Paths.get(path, packageName.replace('.', File.separatorChar));
    }

    /**
     * 转换class文件为类名
     */
    public static String toClassName(String classFileName) {
        return classFileName.replace('/', '.').replace('\\', '.').replace(CLASS_SUFFIX, "");
    }

    /**
     * 扫描指定包下的所有class文件，并使用{@link Class#forName(String)} 转换为class
     * 注意，该方法是扫描ClassLoader的方式
     *
     * @param packageName 包名
     * @param unZipJar    是否扫描jar里的文件
     * @return class集合
     */
    public static Set<Class<?>> scanClass(String packageName, boolean unZipJar) throws Exception {
        return scanClass(packageName, unZipJar, null);
    }

    /**
     * 扫描指定包下的所有class文件，并使用{@link Class#forName(String)} 转换为class
     * 注意，该方法是扫描ClassLoader的方式
     *
     * @param packageName 包名
     * @param unZipJar    是否扫描jar里的文件
     * @param filter      class文件过滤器，如果传入null，则表示获取所有class文件
     * @return class集合
     */
    public static Set<Class<?>> scanClass(String packageName, boolean unZipJar, IClassFilter filter) throws Exception {
        Set<String> clzFileSet = scanClassFile(packageName, unZipJar);
        Set<Class<?>> clzSet = new HashSet<>();
        for (String classFileName : clzFileSet) {
            String className = ScannerHelper.toClassName(classFileName);
            Class<?> clz = Class.forName(className);
            if (filter == null || filter.pass(clz)) {
                clzSet.add(clz);
            }
        }
        return clzSet;
    }

    /**
     * 扫描指定包下面的class文件
     *
     * @param packageName 包名
     * @param unZipJar    是否扫描jar里的文件
     * @return class文件路径集合
     * <br/>外部使用{@link ClassLoader#getResource(String)}获取该文件
     * <br/>也可以使用{@link #toClassName(String)}转换为className
     */
    public static Set<String> scanClassFile(String packageName, boolean unZipJar) throws Exception {
        return scanClassLoader(packageName, CLASS_SUFFIX, unZipJar);
    }

    /**
     * 扫描ClassLoader下的指定后缀名的文件
     *
     * @param packageName 包名
     * @param suffix      后缀名
     * @param unZipJar    是否扫描jar里的文件
     * @return 文件路径集合
     * @throws Exception 扫描异常
     */
    public static Set<String> scanClassLoader(String packageName, String suffix, boolean unZipJar) throws Exception {
        URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        URL[] urls = classLoader.getURLs();
        Set<String> set = new HashSet<>();
        String pkgDirName = packageName.replace('.', File.separatorChar);
        for (URL url : urls) {
            Path path = Paths.get(url.toURI());
            scan(path, set, pkgDirName, suffix, unZipJar);
        }
        return set;
    }

    /**
     * 执行扫描
     */
    private static void scan(Path path, final Set<String> set, final String prefix, final String suffix, final boolean unZipJar) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.toString();
                if (unZipJar && fileName.endsWith(".jar")) {
                    set.addAll(unZipJar(fileName, prefix, suffix));
                } else {
                    fileName = realClassFileName(fileName, prefix);
                    if (pass(fileName, prefix, suffix)) {
                        set.add(fileName);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 处理从classLoader中扫描出的文件，获取真实的class文件名
     */
    public static String realClassFileName(String fileName, String prefix) {
        fileName = sub(fileName, BIN_PREFIX, 5);
        if (!StringUtils.isEmpty(prefix)) {
            fileName = sub(fileName, prefix, 0);
        }
        return fileName;
    }

    /**
     * 剔除前缀
     */
    private static String sub(String fileName, String prefix, int offset) {
        // 处理名称
        int index = fileName.lastIndexOf(prefix);
        return index != -1 ? fileName.substring(index + offset) : fileName;
    }

    /**
     * 检测文件前缀路径与后缀名
     */
    private static boolean pass(String fileName, String prefix, String suffix) {
        return !(prefix != null && !fileName.startsWith(prefix)) && !(suffix != null && !fileName.endsWith(suffix));
    }

    /**
     * 解压jar
     *
     * @param jarName jar名称
     * @param prefix  匹配前缀
     * @param suffix  匹配后缀
     * @return 符合条件的文件集合
     * @throws IOException 解压异常
     */
    private static Set<String> unZipJar(String jarName, String prefix, String suffix) throws IOException {
        Set<String> set = new HashSet<>();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarName);
            prefix = prefix.replace(File.separatorChar, '/');
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = enumeration.nextElement();
                if (jarEntry.isDirectory()) {
                    continue;
                }
                String fileName = jarEntry.getName();
                if (!pass(fileName, prefix, suffix)) {
                    continue;
                }

                set.add(fileName);
            }
        } finally {
            if (jarFile != null) {
                jarFile.close();
            }
        }
        return set;
    }

    /**
     * 列出指定目录下的，所有后缀名匹配的文件
     *
     * @param dirName 目录名
     * @param suffix  后缀名
     * @return 文件列表
     * @throws Exception 文件遍历异常
     */
    public static Set<Path> listFile(String dirName, String suffix) throws Exception {
        Path path = Paths.get(dirName);
        Set<Path> set = new HashSet<>();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (pass(file.toString(), null, suffix)) {
                    set.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return set;
    }

}

package com.ts.framework.script;

import com.ts.framework.file.FileMonitor;
import com.ts.framework.file.FileVersion;
import com.ts.framework.helper.DynamicClassLoader;
import com.ts.framework.helper.ScannerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 脚本工厂
 *
 * @author wl
 */
@Component
public class ScriptManager implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptManager.class);
    private static final String SCRIPT_SUFFIX = ".class";//脚本文件后缀
    private String dir;//脚本目录
    private Map<String, Script> scriptMap = new HashMap<>();//所有脚本文件集合
    private Map<String, ScriptBox> scriptBoxMap = new HashMap<>();//脚本缓存盒集合

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        context.getBeansOfType(ScriptList.class).values().forEach(this::initScriptBox);
        context.getBeansOfType(ScriptProtoTypeMap.class).values().forEach(this::initScriptBox);
    }

    /**
     * 初始化脚本管理器
     */
    private void initScriptBox(ScriptBox scriptBox) {
        scriptBoxMap.put(scriptBox.parent().getName(), scriptBox);
    }

    /**
     * 执行脚本扫描
     *
     * @param dir 脚本目录
     * @throws Exception 扫描中出现异常
     */
    public void scan(String dir) throws Exception {
        this.dir = dir;

        loadScript();

        Path path = Paths.get(dir);
        FileMonitor.watch(path, SCRIPT_SUFFIX, this::reloadScript);

        scriptBoxMap.values().forEach(ScriptBox::scanOver);
    }

    /**
     * 重新加载脚本数据
     */
    public void reload() throws Exception {
        loadScript();
    }

    /**
     * 加载脚本
     */
    private void loadScript() throws Exception {
        Set<Path> files = ScannerHelper.listFile(dir, SCRIPT_SUFFIX);
        DynamicClassLoader classLoader = new DynamicClassLoader();
        files.forEach(path -> loadScript(classLoader, path));
    }

    /**
     * 单个脚本热更
     *
     * @param fileName 热更的文件
     */
    public void reloadScript(String fileName) {
        Path path = Paths.get(fileName);
        loadScript(new DynamicClassLoader(), path);
    }

    /**
     * 加载脚本
     *
     * @param classLoader 加载class文件的classloader
     * @param path        文件地址
     */
    private void loadScript(DynamicClassLoader classLoader, Path path) {
        try {
            if (FileVersion.isRecord(path)) {
                return;//脚本已被加载
            }
            byte[] bytes = Files.readAllBytes(path);
            Class clz = classLoader.load(null, bytes);

            Script script = scriptMap.get(clz.getName());
            if (script == null) {
                //新的脚本
                script = new Script(path, clz);
                scriptMap.put(clz.getName(), script);

                injectScript(script, clz);

                LOGGER.info("load script: {}", clz);
            } else {
                //脚本热更
                script.changeClz(clz);

                LOGGER.info("reload script: {}", clz);
            }

            //记录版本信息
            FileVersion.record(path);
        } catch (Exception e) {
            throw new RuntimeException("load script " + path, e);
        }
    }

    /**
     * 将脚本注入到脚本管理器中
     */
    private void injectScript(Script script, Class clz) throws InstantiationException, IllegalAccessException {
        for (Class aClass : clz.getInterfaces()) {
            tryInject2Box(script, aClass);
        }
        clz = clz.getSuperclass();
        if (clz != null) {
            tryInject2Box(script, clz);
            injectScript(script, clz);
        }
    }

    /**
     * 尝试注入到脚本管理器中
     */
    private void tryInject2Box(Script script, Class clz) throws IllegalAccessException, InstantiationException {
        ScriptBox scriptBox = scriptBoxMap.get(clz.getName());
        if (scriptBox != null) {
            scriptBox.inject(script.getObj());
            script.addScriptBox(scriptBox);
        }
    }

}

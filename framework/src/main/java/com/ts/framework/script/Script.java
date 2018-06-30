package com.ts.framework.script;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 脚本对象
 * @author wl
 */
class Script {
    private Path path;//脚本文件路径
    private Class clz;//脚本
    private Object obj;//脚本实例
    private List<ScriptBox> scriptBoxes = new ArrayList<>(0);//加入到的脚本管理器

    Script(Path path, Class clz) throws InstantiationException, IllegalAccessException {
        this.path = path;
        this.clz = clz;

        changeClz(clz);
    }

    /**
     * 添加脚本缓存记录
     */
    void addScriptBox(ScriptBox scriptBox){
        scriptBoxes.add(scriptBox);
    }

    /**
     * 热更
     * @param clz 新的脚本
     */
    void changeClz(Class clz) throws IllegalAccessException, InstantiationException {
        if (!scriptBoxes.isEmpty()){
            Object newObj = clz.newInstance();
            scriptBoxes.forEach(scriptBox -> scriptBox.reInject(this.obj, newObj));
            this.obj = newObj;
        }

        this.clz = clz;
    }

    /**
     * 获取脚本对象
     */
    Object getObj() throws IllegalAccessException, InstantiationException {
        if (obj == null){
            obj = clz.newInstance();
        }
        return obj;
    }

    /**
     * 获取脚本文件路径
     */
    Path getPath(){
        return path;
    }

}

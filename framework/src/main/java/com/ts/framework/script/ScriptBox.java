package com.ts.framework.script;

/**
 * 脚本盒子，里面存放一系列脚本
 * @author wl
 */
public interface ScriptBox {

    /**
     * 脚本父类，所有子类都将被该盒子拥有
     */
    Class parent();

    /**
     * 注入脚本
     *
     * @param script 脚本对象
     */
    void inject(Object script);

    /**
     * 脚本重新注入（热更）
     *
     * @param oldScript 老的脚本对象
     * @param newScript 新的脚本对象
     */
    void reInject(Object oldScript, Object newScript);

    /**
     * 扫描结束
     */
    void scanOver();

}

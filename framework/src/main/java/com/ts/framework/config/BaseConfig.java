package com.ts.framework.config;

/**
 * 配置接口
 * @author wl
 */
public interface BaseConfig<ConfigType extends Enum<? extends IConfigType>> {

    /**
     * 主键索引，列表数据没有主键返回null就行
     */
    Object configId();

    /**
     * 配置类型
     */
    ConfigType configType();

    /**
     * 解析
     */
    void parse();

}

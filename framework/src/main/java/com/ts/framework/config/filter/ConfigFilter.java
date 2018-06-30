package com.ts.framework.config.filter;


import com.ts.framework.config.BaseConfig;
import com.ts.framework.config.IConfigType;

/**
 * 配置过滤接口
 * @author wl
 */
public interface ConfigFilter<DefType extends Enum<? extends IConfigType>, Config extends BaseConfig<DefType>> {

    // 字段值是否匹配
    boolean pass(Config config, Object targetValue) throws Exception;

}
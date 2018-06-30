package com.ts.framework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置数据缓存
 * @author wl
 */
public class ConfigBox<ConfigType extends Enum<? extends IConfigType>> {
    private static Logger logger = LoggerFactory.getLogger(ConfigBox.class);
    private Enum<? extends IConfigType> defType;
    private ArrayList<BaseConfig<ConfigType>> all = new ArrayList<>();
    private Map<Object, BaseConfig<ConfigType>> keyDefMap = new HashMap<>();// 主键id对应map

    public void clear() {
        all.clear();
        keyDefMap.clear();
    }

    public void clear(Class<?> defClass) {
        ArrayList<BaseConfig<ConfigType>> newList = new ArrayList<>();
        for (BaseConfig<ConfigType> baseConfig : all) {
            if (baseConfig.getClass() != defClass) {
                newList.add(baseConfig);
            } else {
                keyDefMap.remove(baseConfig.configId());
            }
        }
        if (newList.size() != all.size()) {
            logger.debug("remove " + defClass + ": " + (all.size() - newList.size()));
        }
        all = newList;
    }

    /**
     * 添加一条配置
     */
    public void add(BaseConfig<ConfigType> config) {
        if (config == null) {
            throw new NullPointerException();
        }
        Object key = config.configId();
        if (key != null) {
            if (keyDefMap.put(key, config) != null) {
                throw new RuntimeException("exist config " + defType + ":" + config.configId() + " in keyDefMap");
            }
        }
        all.add(config);
    }

    /**
     * 根据主键获取
     */
    public BaseConfig<ConfigType> get(Object key) {
        return keyDefMap.get(key);
    }

    /**
     * 配置数量
     */
    public int size() {
        return all.size();
    }

    /**
     * 获取所有
     */
    @SuppressWarnings("unchecked")
    public <Config extends BaseConfig<ConfigType>> List<Config> getAll() {
        return (List<Config>) all;
    }

    public Map<Object, BaseConfig<ConfigType>> getKeyDefMap() {
        return keyDefMap;
    }

    public void setAll(ArrayList<BaseConfig<ConfigType>> all) {
        this.all = all;
    }

    public Enum<? extends IConfigType> getDefType() {
        return defType;
    }

    public void setDefType(Enum<? extends IConfigType> defType) {
        this.defType = defType;
    }

    public void setKeyDefMap(Map<Object, BaseConfig<ConfigType>> keyDefMap) {
        this.keyDefMap = keyDefMap;
    }

}

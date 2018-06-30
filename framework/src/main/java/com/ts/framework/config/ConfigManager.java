package com.ts.framework.config;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 配置工厂
 *
 * @author wl
 */
@Component
public class ConfigManager {
    // private static Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    private ConfigBox<?>[] configBoxes;// 所有配置缓存
    private Set<ConfigIndex<?, ?>> indexes = new HashSet<>();// 索引集合

    /**
     * 初始化配置容器
     */
    public <ConfigType extends Enum<? extends IConfigType>> void init(ConfigType[] configTypes) {
        configBoxes = new ConfigBox[configTypes.length];
        for (Enum<? extends IConfigType> defType : configTypes) {
            configBoxes[defType.ordinal()] = new ConfigBox<>();
            configBoxes[defType.ordinal()].setDefType(defType);
        }
    }

    // 获取配置集合
    @SuppressWarnings("unchecked")
    private <ConfigType extends Enum<? extends IConfigType>> ConfigBox<ConfigType> getBox(ConfigType type) {
        ConfigBox<ConfigType> box = (ConfigBox<ConfigType>) configBoxes[type.ordinal()];
        if (box == null) {
            box = new ConfigBox<>();
            box.setDefType(type);
            configBoxes[type.ordinal()] = box;
        }
        return box;
    }

    /**
     * 放入数据到配置缓存，如果id主键的映射有旧值，则将新配置数据反射到原来的def上
     *
     * @param config 配置
     */
    public <ConfigType extends Enum<? extends IConfigType>, Config extends BaseConfig<ConfigType>> void put(Config config) throws Exception {
        try {
            config.parse();
        } catch (Exception e) {
            throw new RuntimeException(config.getClass().toString(), e);
        }
        ConfigType type = config.configType();
        ConfigBox<ConfigType> configBox = getBox(type);
        configBox.add(config);
    }

    /**
     * 所有数据
     */
    public ConfigBox<?>[] getDefBoxes() {
        return configBoxes;
    }

    /**
     * a
     * 填入所有数据
     */
    public void setAll(ConfigBox<?>[] configBoxes) {
        this.configBoxes = configBoxes;
    }

    /**
     * 索引注册
     */
    public void registerIndex(ConfigIndex<?, ?> index) {
        indexes.add(index);
    }

    /**
     * 根据主键获取
     */
    @SuppressWarnings("unchecked")
    public <ConfigType extends Enum<? extends IConfigType>, Config extends BaseConfig<ConfigType>> Config get(ConfigType type, Object key) {
        return (Config) getBox(type).get(key);
    }

    /**
     * 获取指定类型配置的所有数据
     */
    public <ConfigType extends Enum<? extends IConfigType>, Config extends BaseConfig<ConfigType>> List<Config> getAll(ConfigType type) {
        return getBox(type).getAll();
    }

    /**
     * 清除索引
     */
    public <ConfigType extends Enum<? extends IConfigType>> void clearIndex(ConfigType configType, Class<?> defClass) {
        // 清除旧配置
        getBox(configType).clear(defClass);
        // 清除索引
        indexes.stream().filter(index -> index.configType() == configType).forEach(ConfigIndex::clear);
    }

    /**
     * 清除所有配置数据
     */
    public void clearAll() {
        for (ConfigBox<?> configBox : configBoxes) {
            configBox.clear();
        }
        indexes.clear();
    }

    /**
     * 指定类型配置的数量
     */
    public int size(Enum<? extends IConfigType> type) {
        return getBox(type).size();
    }
}

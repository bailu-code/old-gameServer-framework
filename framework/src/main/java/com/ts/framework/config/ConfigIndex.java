package com.ts.framework.config;

import com.ts.framework.config.filter.ConfigFilter;
import com.ts.framework.config.filter.GroupFilter;
import com.ts.framework.helper.StringHelper;
import com.ts.framework.spring.Context;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 配置索引参数
 * <p>
 *
 * @author wl
 */
public class ConfigIndex<ConfigType extends Enum<? extends IConfigType>, Config extends BaseConfig<ConfigType>> {
    private static Logger logger = LoggerFactory.getLogger(ConfigIndex.class);
    private static AtomicInteger INT_INDEX = new AtomicInteger();

    class Filter {
        ConfigFilter<ConfigType, Config> configFilter;// 过滤器
        boolean negation;// 结果集取反
        Object defaultValue;// 默认值
    }

    private ConfigType configType;
    private int index;// 该索引编号
    private Class<?> configClass;// 配置类
    private Method sortField;// 排序字段
    private Map<String, ArrayList<BaseConfig<ConfigType>>> indexMap = new HashMap<>();// 索引集合

    private ArrayList<Filter> filters = new ArrayList<>();// 过滤列表

    public ConfigIndex(Class<?> configClass, ConfigType configType) {
        this.configClass = configClass;
        this.configType = configType;
        this.index = INT_INDEX.incrementAndGet();
    }

    @Override
    public int hashCode() {
        return index;
    }

    /**
     * 新建一个索引
     */
    public static <ConfigType extends Enum<? extends IConfigType>, Config extends BaseConfig<ConfigType>> ConfigIndex<ConfigType, Config> create(Class<Config> configClass,
                                                                                                                                                 ConfigType configType) {
        return new ConfigIndex<>(configClass, configType);
    }

    /**
     * 添加过滤器
     */
    public ConfigIndex<ConfigType, Config> addFilter(ConfigFilter<ConfigType, Config> configFilter) {
        Filter filter = new Filter();
        filter.configFilter = configFilter;
        filters.add(filter);
        return this;
    }

    /**
     * 添加过滤字段
     */
    public ConfigIndex<ConfigType, Config> group(String fieldName) {
        GroupFilter<ConfigType, Config> groupFilter = new GroupFilter<>();
        groupFilter.field = FieldUtils.getField(configClass, fieldName, true);
        if (groupFilter.field == null) {
            throw new RuntimeException("not found field " + fieldName + " by " + configClass);
        }

        addFilter(groupFilter);
        return this;
    }

    /**
     * 设置该字段的默认值
     */
    public ConfigIndex<ConfigType, Config> defaultValue(Object defaultValue) {
        filters.get(filters.size() - 1).defaultValue = defaultValue;
        return this;
    }

    /**
     * 设置该字段取反
     */
    public ConfigIndex<ConfigType, Config> negation() {
        filters.get(filters.size() - 1).negation = true;
        return this;
    }

    /**
     * 添加排序字段
     */
    public ConfigIndex<ConfigType, Config> sort(String fieldName) {
        this.sortField = MethodUtils.getMatchingAccessibleMethod(configClass, fieldName);
        return this;
    }

    /**
     * 清空索引
     */
    public void clear() {
        indexMap.clear();
    }

    /**
     * 获取索引列表，直接返回的原始list，请调用者不要直接修改
     */
    @SuppressWarnings("unchecked")
    public List<Config> get(Object... values) {
        String key = StringHelper.buildKey(values);
        ArrayList<BaseConfig<ConfigType>> list = indexMap.get(key);
        if (list == null) {
            list = new ArrayList<>();
            // 过滤
            ConfigManager configManager = Context.getBean(ConfigManager.class);
            List<Config> all = configManager.getAll(configType);
            for (Config baseConfig : all) {
                if (baseConfig.getClass() != configClass) {
                    continue;
                }
                if (pass(baseConfig, values)) {
                    list.add(baseConfig);
                }
            }

            // 排序
            if (sortField != null) {
                sort(list);
            }

            list.trimToSize();
            indexMap.put(key, list);

            configManager.registerIndex(this);
        }
        return (List<Config>) list;
    }

    /**
     * 指定查询条件的配置是否存在
     */
    public boolean has(Object... values) {
        return !get(values).isEmpty();
    }

    /**
     * 获取单条
     */
    public Config getOne(Object... values) {
        List<Config> configs = get(values);
        return configs.isEmpty() ? null : configs.get(0);
    }

    /**
     * 检测指定配置是否满足需求
     */
    private boolean pass(Config config, Object... values) {
        try {
            int index = 0;
            for (Filter filter : filters) {
                Object value = filter.defaultValue != null ? filter.defaultValue : values[index++];
                if (filter.negation) {
                    if (filter.configFilter.pass(config, value)) {
                        return false;
                    }
                } else {
                    if (!filter.configFilter.pass(config, value)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("filter config " + config.configType() + "error!", e);
            return false;
        }
    }

    /**
     * 按字段排序
     */
    private void sort(List<BaseConfig<ConfigType>> configs) {
        try {
            if (configs == null || configs.size() <= 0) {
                return;
            }
            final Method sortMethod = sortField;

            configs.sort((o1, o2) -> {
                try {
                    Object o1Value = sortMethod.invoke(o1);
                    Object o2Value = sortMethod.invoke(o2);
                    return o1Value.toString().compareTo(o2Value.toString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> configClass() {
        return configClass;
    }

    public Enum<? extends IConfigType> configType() {
        return configType;
    }

}
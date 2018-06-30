package com.ts.framework.config;

import com.ts.framework.helper.ClassHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;


/**
 * 离散数据配置基类
 * 
 * @author wl
 */
public abstract class BaseDiscrete {
	private static Logger logger = LoggerFactory.getLogger(BaseDiscrete.class);
	private static Set<String> initSet = new HashSet<>();

	/**
	 * 利用反射设置config的值
	 */
	public static void reflectConfig(Class<? extends BaseDiscrete> configClass, String key, String valueString) throws Exception {
		Field field;
		try {
			// 鉴别配置key
			field = configClass.getDeclaredField(key);
		} catch (Exception e) {
			// logger.error("not find field {} from {}", key, configClass);
			return;
		}

		try {
			// 取得值类型
			// 转换值类型，映射设置值
			Object value = ClassHelper.parseValue(field.getType(), valueString);
			field.setAccessible(true);
			field.set(null, value);

			initSet.add(key);
			logger.debug("reflect config field {} = {}", key, value);
		} catch (Exception e) {
			throw new RuntimeException(field.getName(), e);
		}
	}

	/**
	 * 反射设置configClass的值
	 */
	public static void reflectConfig(Class<? extends BaseDiscrete> configClass, String key, Object value) throws Exception {
		Field field = configClass.getDeclaredField(key);
		field.setAccessible(true);
		field.set(null, value);

		initSet.add(key);
		logger.debug("reflect config field {} = {}", key, value);
	}

	/**
	 * 加载配置文件
	 */
	public static void loadProperties(Class<? extends BaseDiscrete> configClass, String filePath) throws Exception {
		Properties p = new Properties();
		p.load(ClassLoader.getSystemResource(filePath).openStream());
		for (Entry<Object, Object> entry : p.entrySet()) {
			reflectConfig(configClass, entry.getKey().toString(), entry.getValue().toString());
		}
	}

	/**
	 * 检测配置是否初始化
	 */
	public static void checkInit(Class<? extends BaseDiscrete> configClass) {
		Field[] fields = configClass.getDeclaredFields();
		for (Field field : fields) {
			if (!initSet.contains(field.getName())) {
				throw new RuntimeException(configClass + " not start BaseDiscrete key = " + field.getName());
			}
		}
	}

}

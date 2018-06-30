package com.ts.framework.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * class相关处理、检测
 * @author wl
 */
public class ClassHelper {

	/**
	 * 获得指定类的父类的泛型参数的实际类型
	 * 
	 * @param clazz
	 *            Class
	 * @param index
	 *            泛型参数所在索引,从0开始
	 * @return Class
	 */
	@SuppressWarnings("rawtypes")
	public static Class getSuperClassGenericType(Class clazz, int index) {
		if (clazz == null) {
			return null;
		}

		Type genericType = clazz.getGenericSuperclass();
		while ((genericType != null) && (!(genericType instanceof ParameterizedType))) {
			clazz = clazz.getSuperclass();
			if (clazz == null) {
				break;
			}
			genericType = clazz.getGenericSuperclass();
		}

		if (!(genericType instanceof ParameterizedType)) {
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genericType).getActualTypeArguments();
		if ((params != null) && (index >= 0) && (index < params.length) && ((params[index] instanceof Class))) {
			return (Class) params[index];
		}

		return Object.class;
	}

	/**
	 * 判断childClass是否为superclass的子类
	 */
	public static boolean checkSuperclass(Class<?> childClass, Class<?> superclass) {
		if ((childClass == null) || (superclass == null)) {
			return false;
		}
		if (childClass.equals(superclass)) {
			return true;
		}
		Class<?> clazz = childClass.getSuperclass();
		while (clazz != null) {
			if (clazz.equals(superclass)) {
				return true;
			}
			clazz = clazz.getSuperclass();
		}
		return false;
	}

	/**
	 * 检测制定类是否实现指定接口
	 */
	public static boolean checkInterface(Class<?> checkClass, Class<?> interClass) {
		if ((checkClass == null) || (interClass == null)) {
			return false;
		}
		if (checkClass.equals(interClass)) {
			return true;
		}
		Class<?>[] classes = checkClass.getInterfaces();
		for (Class<?> clz : classes) {
			if (checkInterface(clz, interClass)) {
				return true;
			}
		}
		Class<?> superClz = checkClass.getSuperclass();
		return checkInterface(superClz, interClass);
	}

	/**
	 * 转移属性
	 */
	public static void distractField(Object srcObj, Object decObj) throws Exception {
		if (!srcObj.getClass().getName().equals(decObj.getClass().getName())) {
			throw new RuntimeException(srcObj.getClass() + ", " + decObj.getClass() + " not same class");
		}
		Class<?> clazz = srcObj.getClass();
		while (clazz != null) {
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isFinal(field.getModifiers())) {
					continue;
				}
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				field.setAccessible(true);
				field.set(decObj, field.get(srcObj));
			}
			clazz = clazz.getSuperclass();
		}
	}

	/**
	 * 根据valueClass，解析字符串，支持int int[] long long[] float float[] double double[]
	 * float enum string map(格式:key-value;key-value)
	 */
	public static Object parseValue(Class<?> valueClass, String valueString) throws Exception {
		if (valueString == null || "".equals(valueString)) {
			return null;
		}
		if (valueClass == int.class) {
			return Integer.valueOf(valueString);
		} else if (valueClass == int[].class) {
			return StringHelper.toIntArray(StringHelper.findSplit(valueString), valueString);
		} else if (valueClass == long.class) {
			return Long.valueOf(valueString);
		} else if (valueClass == long[].class) {
			return StringHelper.toLongArray(StringHelper.findSplit(valueString), valueString);
		} else if (valueClass == float.class) {
			return Float.valueOf(valueString);
		} else if (valueClass == float[].class) {
			return StringHelper.toFloatArray(StringHelper.findSplit(valueString), valueString);
		} else if (valueClass == boolean.class) {
			return Boolean.valueOf(valueString);
		} else if (valueClass == double.class) {
			return Double.valueOf(valueString);
		} else if (valueClass == double[].class) {
			return StringHelper.toDoubleArray(StringHelper.findSplit(valueString), valueString);
		} else if (valueClass == String[].class) {
			return StringHelper.toArray(valueString);
		} else if (valueClass == String.class) {
			return valueString;
		} else if (checkInterface(valueClass, Map.class)) {
			Class<?> keyC = getSuperClassGenericType(valueClass, 0);
			Class<?> valueC = getSuperClassGenericType(valueClass, 1);
			String[] strings = valueString.split(StringHelper.SPLIT_2);
			HashMap<Object, Object> map = new HashMap<>();
			for (String str : strings) {
				String[] one = str.split(StringHelper.SPLIT_3);
				map.put(parseValue(keyC, one[0]), parseValue(valueC, one[1]));
			}
			return map;
		} else if (valueClass.isEnum()) {
			Object value;
			Class<?> valueC;
			try {
				value = parseValue(int.class, valueString);
				valueC = int.class;
			} catch (Exception e) {
				value = parseValue(String.class, valueString);
				valueC = String.class;
			}

			Method method = valueClass.getMethod("valueOf", valueC);
			if (method == null) {
				throw new NullPointerException("class " + valueClass + ", enum not have valueOf(int) " + valueString);
			}

			return method.invoke(null, value);
		} else {
			throw new RuntimeException("Unable to identify " + valueClass + ", value: " + valueString);
		}
	}

}

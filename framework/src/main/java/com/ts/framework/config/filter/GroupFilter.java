package com.ts.framework.config.filter;

import com.ts.framework.config.BaseConfig;
import com.ts.framework.config.IConfigType;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;

/**
 * 字段分组
 * 
 * @author wl
 */
public class GroupFilter<DefType extends Enum<? extends IConfigType>, Def extends BaseConfig<DefType>> implements ConfigFilter<DefType, Def> {
	public Field field;// 字段

	@Override
	public boolean pass(Def def, Object targetValue) throws Exception {
		Object defValue = field.get(def);
		return valueEquals(defValue, targetValue);
	}

	private boolean valueEquals(Object defValue, Object targetValue) {
		Class<?> valueClass = defValue.getClass();
		if (!valueClass.isArray()) {
			return defValue.equals(targetValue);
		}

		// 数组
		if (valueClass == int[].class) {
			return ArrayUtils.contains((int[]) defValue, (int) targetValue);
		} else if (valueClass == long[].class) {
			return ArrayUtils.contains((long[]) defValue, (long) targetValue);
		} else if (valueClass == float[].class) {
			return ArrayUtils.contains((float[]) defValue, (float) targetValue);
		} else if (valueClass == boolean[].class) {
			return ArrayUtils.contains((boolean[]) defValue, (boolean) targetValue);
		} else if (valueClass == double[].class) {
			return ArrayUtils.contains((double[]) defValue, (double) targetValue);
		} else if (valueClass == String[].class) {
			return ArrayUtils.contains((String[]) defValue, targetValue);
		} else if (valueClass.isEnum()) {
			return ArrayUtils.contains((Enum[]) defValue, targetValue);
		} else {
			throw new RuntimeException("Unable to identify " + valueClass + ", value: " + defValue);
		}
	}

}
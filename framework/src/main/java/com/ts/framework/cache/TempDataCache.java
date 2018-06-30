package com.ts.framework.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 临时数据缓存
 * @author wl
 */
public class TempDataCache<Type extends ITempDataType>  {
	private Map<Object, TempData> tempDataMap = new ConcurrentHashMap<>();// 临时数据缓存

	class TempData {
		private long addTime;// 添加进来的时间
		private int lifeTime;// 生命周期
		private Object object;// 数据

		TempData(Object object, int lifeTime) {
			this.addTime = System.currentTimeMillis();
			this.lifeTime = lifeTime;
			this.object = object;
		}

		/**
		 * 获取缓存数据，如果超时则返回null
		 */
		public Object getObject() {
			if (lifeTime == 0 || System.currentTimeMillis() - addTime < lifeTime) {
				return object;
			}
			return null;
		}

		/**
		 * 获取临时数据的剩余存在时间，如果已过期，返回0，如果生命周期为0（一直存在），则抛出异常
		 */
		public int getRemainTime() {
			if (lifeTime == 0) {
				throw new RuntimeException("life time is 0");
			}
			long passTime = System.currentTimeMillis() - addTime;
			if (passTime >= lifeTime) {
				return 0;
			}
			return (int) (lifeTime - passTime);
		}
	}

	/**
	 * 清除所有缓存数据
	 */
	public void tempDataClear() {
		tempDataMap.clear();
	}

	/**
	 * 添加临时数据
	 * 
	 * @param tempDataType
	 *            临时数据类型
	 * @param data
	 *            数据
	 * @param lifeTime
	 *            生命周期，如果不超时，则传入0
	 */
	public <T> void tempDataAdd(Type tempDataType, T data, int lifeTime) {
		TempData tempData = tempDataMap.get(tempDataType);
		if (tempData == null) {
			tempData = new TempData(data, lifeTime);
			tempDataMap.put(tempDataType, tempData);
		} else {
			tempData.addTime = System.currentTimeMillis();
			tempData.lifeTime = lifeTime;
			tempData.object = data;
		}
	}

	/**
	 * 获取临时数据
	 * 
	 * @param tempDataType
	 *            临时数据类型
	 * @return 与key关联的数据，不存在或超时则返回null
	 */
	@SuppressWarnings("unchecked")
	public <T> T tempDataGet(Type tempDataType) {
		TempData tempData = tempDataMap.get(tempDataType);
		return (T) (tempData != null ? tempData.getObject() : null);
	}

	/**
	 * 当前临时缓存是否含有指定type未过期的数据
	 *
	 * @param tempDataType 临时数据类型
	 * @return 数据存在并且未过期true
	 */
	public boolean tempDataHas(Type tempDataType) {
		TempData tempData = tempDataMap.get(tempDataType);
		return tempData != null && tempData.getObject() != null;
	}

	/**
	 * 该状态的剩余时间
	 * 
	 * @param tempDataType 临时数据类型
	 * @return 获取数据剩余过期时间，已过期返回0
	 */
	public int tempDataRemainTime(Type tempDataType) {
		TempData tempData = tempDataMap.get(tempDataType);
		if (tempData == null) {
			return 0;
		}
		return tempData.getRemainTime();
	}

	/**
	 * 移除与指定type关联的数据
	 * 
	 * @param tempDataType 临时数据类型
	 * @return 返回被移除的数据（如果存在 ）
	 */
	@SuppressWarnings("unchecked")
	public <T> T tempDataRemove(Type tempDataType) {
		TempData tempData = tempDataMap.remove(tempDataType);
		return (T) (tempData != null ? tempData.getObject() : null);
	}

}

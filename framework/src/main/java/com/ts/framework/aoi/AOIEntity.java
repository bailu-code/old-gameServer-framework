package com.ts.framework.aoi;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 场景中的对象
 *
 * @author wl
 */
public abstract class AOIEntity implements Serializable {
	private static final long serialVersionUID = 5417017451141243894L;
	public float x, y, z;
	public float aspect;// 朝向

	private transient Map<Long, AOIEntity> viewMap = new HashMap<>();// 视野范围内的单位

	public AOIEntity() {
	}

	/**
	 * 唯一id
	 */
	public abstract long getId();

	/**
	 * 设置当前坐标
	 * @param aspect
	 *            朝向
	 */
	public void setLocation(float x, float y, float z, float aspect) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.aspect = aspect;
	}

	/**
	 * copy当前位置
	 */
	public void setLocation(AOIEntity copy) {
		this.x = copy.x;
		this.y = copy.y;
		this.z = copy.z;
		this.aspect = copy.aspect;
	}

	/**
	 * 是否为同一个位置
	 */
	public boolean samePlace(AOIEntity other) {
		return this.x == other.x && this.y == other.y;
	}

	/**
	 * 设置当前坐标
	 */
	public void setLocation(float[] point, float aspect) {
		this.x = point[0];
		this.y = point[1];
		this.z = point[2];
		this.aspect = aspect;
	}

	/**
	 * 添加单位进入视野
	 */
	public void addEntity(AOIEntity aoiEntity) {
		viewMap.put(aoiEntity.getId(), aoiEntity);
	}

	/**
	 * 将指定单位移除视野
	 */
	public void removeEntity(long entityId) {
		viewMap.remove(entityId);
	}

	/**
	 * 返回视野范围内的所有单位
	 */
	@SuppressWarnings("unchecked")
	public <T extends AOIEntity> Collection<T> getAllViews() {
		return (Collection<T>) viewMap.values();
	}

	/**
	 * 获取视野内的指定单位
	 */
	@SuppressWarnings("unchecked")
	public <T extends AOIEntity> T getUnit(long unitId) {
		return (T) viewMap.get(unitId);
	}

	/**
	 * 清除视野
	 */
	public void clearView() {
		viewMap.clear();
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getAspect() {
		return aspect;
	}

	public void setAspect(float aspect) {
		this.aspect = aspect;
	}

}

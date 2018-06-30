package com.ts.framework.aoi;

import java.util.List;

/**
 * 视野观察者接口
 * @author wl
 */
public interface AOIWatch<Entity extends AOIEntity> {

	/**
	 * incomer进入watcher的视野
	 * 
	 * @param watchers
	 *            观察到进入的单位列表
	 * @param incomer
	 *            进入者
	 */
	void enter(List<Entity> watchers, Entity incomer);

	/**
	 * leaver离开watcher的视野
	 * 
	 * @param watchers
	 *            观察到离开的单位列表
	 * @param leaver
	 *            离开者
	 */
	 void leave(List<Entity> watchers, Entity leaver);

	/**
	 * mover在watcher视野范围内移动
	 * 
	 * @param watchers
	 *            观察到移动的单位列表
	 * @param mover
	 *            移动者
	 */
	 void move(List<Entity> watchers, Entity mover);

}

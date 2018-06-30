package com.ts.framework.aoi;

/**
 * 视野节点
 * @author wl
 */
public class AOINode {
	AOIEntity entity;

	AOINode xPreNode = null;// x轴上一节点
	AOINode xNextNode = null;// x轴下一节点

	AOINode yPreNode = null;// y轴上一节点
	AOINode yNextNode = null;// y轴下一节点

	public AOINode(AOIEntity entity) {
		this.entity = entity;
	}

	public AOIEntity getEntity() {
		return entity;
	}

	public float getX() {
		return entity.x;
	}

	public float getY() {
		return entity.y;
	}

	public void moveTo(float x, float y) {
		entity.x = x;
		entity.y = y;
	}

}
